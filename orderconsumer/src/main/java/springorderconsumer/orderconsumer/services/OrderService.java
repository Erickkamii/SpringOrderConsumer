package springorderconsumer.orderconsumer.services;

import java.math.BigDecimal;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import springorderconsumer.orderconsumer.controller.dto.OrderResponse;
import springorderconsumer.orderconsumer.entity.OrderEntity;
import springorderconsumer.orderconsumer.entity.OrderItem;
import springorderconsumer.orderconsumer.listener.dto.OrderCreatedEvent;
import springorderconsumer.orderconsumer.repository.OrderRepository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate){
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEvent event){
        
        var entity = new OrderEntity();
        entity.setOrderId(event.orderCode());
        entity.setCustomerID(event.clientCode());
        entity.setItems(getOrderItem(event));
        entity.setTotal(getTotal(event));

        orderRepository.save(entity);
    }

    public Page<OrderResponse> findAllByCustomerID(Long customerID, PageRequest pageRequest){
        var orders = orderRepository.findAllByCustomerID(customerID, pageRequest);
        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerID(Long customerID){
        var aggregations = newAggregation(
            match(Criteria.where("customerID").is(customerID)),
            group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregations,"tb_orders",Document.class);
        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.items().stream().map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity()))).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItem(OrderCreatedEvent event){
        return event.items().stream().map(i -> new OrderItem(i.product(), i.quantity(), i.price())).toList();
    }
}

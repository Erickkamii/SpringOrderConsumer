package springorderconsumer.orderconsumer.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import springorderconsumer.orderconsumer.entity.OrderEntity;
import springorderconsumer.orderconsumer.entity.OrderItem;
import springorderconsumer.orderconsumer.listener.dto.OrderCreatedEvent;
import springorderconsumer.orderconsumer.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public void save(OrderCreatedEvent event){
        
        var entity = new OrderEntity();
        entity.setOrderId(event.orderCode());
        entity.setCustomerID(event.clientCode());
        entity.setItems(getOrderItem(event));
        entity.setTotal(getTotal(event));

        orderRepository.save(entity);
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.items().stream().map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity()))).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItem(OrderCreatedEvent event){
        return event.items().stream().map(i -> new OrderItem(i.product(), i.quantity(), i.price())).toList();
    }
}

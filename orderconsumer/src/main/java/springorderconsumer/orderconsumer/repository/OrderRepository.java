package springorderconsumer.orderconsumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import springorderconsumer.orderconsumer.entity.OrderEntity;

public interface OrderRepository extends MongoRepository<OrderEntity, Long> {

}

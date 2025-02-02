package springorderconsumer.orderconsumer.controller.dto;

import java.math.BigDecimal;

import springorderconsumer.orderconsumer.entity.OrderEntity;

public record OrderResponse(Long orderId,
                            Long customerID,
                            BigDecimal total) {
    public static OrderResponse fromEntity(OrderEntity entity){
        return new OrderResponse(entity.getOrderId(), entity.getCustomerID(), entity.getTotal());
    }
}

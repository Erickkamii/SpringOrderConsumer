package springorderconsumer.orderconsumer.controller;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springorderconsumer.orderconsumer.controller.dto.ApiResponse;
import springorderconsumer.orderconsumer.controller.dto.OrderResponse;
import springorderconsumer.orderconsumer.controller.dto.PaginationResponse;
import springorderconsumer.orderconsumer.services.OrderService;


@RestController
public class OrderController {

    private final OrderService orderService;
    
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/customer/{customerID}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(@PathVariable("customerID") Long customerID,
                                                                 @RequestParam(name = "page", defaultValue="0")Integer page,
                                                                 @RequestParam(name = "pageSize", defaultValue="10")Integer pageSize){
        
        var pageResponse = orderService.findAllByCustomerID(customerID, PageRequest.of(page,pageSize));
        var totalOnOrders = orderService.findTotalOnOrdersByCustomerID(customerID);
        
        return ResponseEntity.ok(new ApiResponse<>(
            Map.of("totalOnOrders", totalOnOrders),
            pageResponse.getContent(),
            PaginationResponse.fromPage(pageResponse)
            ));
    }
    
    
}

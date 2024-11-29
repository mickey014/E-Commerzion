package com.codewithkirk.OrderItemsService.Controller;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import com.codewithkirk.OrderItemsService.Exception.OrderItemsException;
import com.codewithkirk.OrderItemsService.Model.OrderItems;
import com.codewithkirk.OrderItemsService.Service.OrderItemsService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class OrderItemsController {

    private final OrderItemsService orderItemsService;

    @PostMapping("/orderItems")
    public ResponseEntity<?> createOrderItems(@RequestBody OrderItemsDto orderItemsDto) {
        try {
            orderItemsService.createOrderItems(orderItemsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with Service: " +  e.getMessage());
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/orderItems/{orderItemsId}")
    public ResponseEntity<?> getOrderItemsById(@PathVariable Long orderItemsId) {
        try {
            Optional<OrderItems> order = orderItemsService.getOrderItemsById(orderItemsId);
            OrderItems foundOrder = order.get();
            OrderItemsDto orderItemsDto = new OrderItemsDto(
                    foundOrder.getOrderItemsId(),
                    foundOrder.getOrderId(),
                    foundOrder.getCustomerId(),
                    foundOrder.getProductId(),
                    foundOrder.getQuantity(),
                    foundOrder.getUnitPrice(),
                    foundOrder.getTotalPrice()
            );
            return ResponseEntity.ok(orderItemsDto);
        } catch (OrderItemsException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/orderItems/customer/{customerId}")
    public ResponseEntity<?> getAllOrderItemsDetailsByCustomerId(@PathVariable Long customerId,
                                              OrderItems orderItemsDto) {
        try {
            List<OrderItems> orders = orderItemsService.getAllOrderItemsDetailsByCustomerId(customerId);
            return ResponseEntity.ok(orders);
        } catch (OrderItemsException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

}

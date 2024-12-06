package com.codewithkirk.orderService.Controller;

import com.codewithkirk.orderService.Dto.OrderDto;
import com.codewithkirk.orderService.Dto.OrderDtoNew;
import com.codewithkirk.orderService.Dto.OrderItemsDto;
import com.codewithkirk.orderService.Exception.OrderException;
import com.codewithkirk.orderService.Model.Orders;
import com.codewithkirk.orderService.Service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        try {
            orderService.createOrder(orderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        } catch (OrderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {

        try {
            Optional<Orders> order = orderService.getByOrderId(orderId);
            Orders foundOrder = order.get();
            OrderDto orderDto = new OrderDto(
                    foundOrder.getOrderId(),
                    foundOrder.getCustomerId(),
                    foundOrder.getProductId(),
                    foundOrder.getTotalAmount(),
                    foundOrder.getOrderStatus(),
                    foundOrder.getShippingAddress(),
                    foundOrder.getPaymentMethod(),
                    foundOrder.getPaymentStatus(),
                    foundOrder.getShippingMethod(),
                    foundOrder.getTrackingNumber()
            );
            return ResponseEntity.ok(orderDto);
        } catch (OrderException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/orders/customer/{customerId}")
    public ResponseEntity<?> getAllOrderDetailsByCustomerId(@PathVariable Long customerId) {

        try {
            List<Orders> orders = orderService.getAllOrderDetailsByCustomerId(customerId);
            return ResponseEntity.ok(orders);
        } catch (OrderException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/orders/customer/{customerId}/order/{orderId}")
    public ResponseEntity<?> getOrderDetailsByCustomerIdAndOrderId(@PathVariable Long customerId,
                                            @PathVariable String orderId) {

        try {
            Optional<Orders> order = orderService.getOrderDetailsByCustomerIdAndOrderId(customerId, orderId);
            Orders foundOrder = order.get();
            OrderDto orderDto = new OrderDto(
                foundOrder.getOrderId(),
                foundOrder.getCustomerId(),
                foundOrder.getProductId(),
                foundOrder.getTotalAmount(),
                foundOrder.getOrderStatus(),
                foundOrder.getShippingAddress(),
                foundOrder.getPaymentMethod(),
                foundOrder.getPaymentStatus(),
                foundOrder.getShippingMethod(),
                foundOrder.getTrackingNumber()
            );
            return ResponseEntity.ok(orderDto);
        } catch (OrderException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/orders/customer/{customerId}/order/{orderId}")
    public ResponseEntity<?> updateOrder(
                                            @PathVariable Long customerId,
                                            @PathVariable String orderId,
                                           @RequestBody OrderDto orderDto) {

        try {
            orderService.updateOrder(customerId, orderId, orderDto);
            return ResponseEntity.ok(null);
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        }catch (OrderException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }
}

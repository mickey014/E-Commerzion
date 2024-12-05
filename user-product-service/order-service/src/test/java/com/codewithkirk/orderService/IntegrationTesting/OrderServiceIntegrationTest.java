package com.codewithkirk.orderService.IntegrationTesting;

import com.codewithkirk.orderService.Dto.OrderDto;
import com.codewithkirk.orderService.IntegrationTesting.Config.OrderServiceIntegrationTestConfig;
import com.codewithkirk.orderService.Model.Orders;
import com.codewithkirk.orderService.Repository.OrderRepository;
import com.codewithkirk.orderService.Service.impl.OrderServiceImpl;
import com.codewithkirk.orderService.ServiceClient.UserServiceClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class OrderServiceIntegrationTest extends OrderServiceIntegrationTestConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    private OrderDto orderDto;
    private Orders newOrder;

    private static String orderId; // Shared field for the orderId
    private static Long customerId = 1L;    // Shared field for customerId

    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingMethod;
    private String trackingNumber;


    @BeforeEach
    void setUp() {
        orderId = "4a202918";
        totalAmount = BigDecimal.valueOf(200.50);
        orderStatus = "Pending";
        shippingAddress = "1234 Elm Street, Some City, Some Country";
        paymentMethod = "Credit Card";
        paymentStatus = "Paid";
        shippingMethod = "Standard";
        trackingNumber = orderServiceImpl.generateTrackingNumber();

        orderDto = new OrderDto(
                orderId,
                customerId,
                totalAmount,
                orderStatus,
                shippingAddress,
                paymentMethod,
                paymentStatus,
                shippingMethod,
                trackingNumber
        );

        newOrder = Orders.builder()
                .orderId(orderId)
                .customerId(customerId)
                .totalAmount(totalAmount)
                .orderStatus(orderStatus)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .shippingMethod(shippingMethod)
                .trackingNumber(trackingNumber)
                .build();
    }

    //@Test
    void testDbConn() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);
        if(result != null) {
            System.out.println(result);
            System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");
        }
    }

//    @Test
//    @Order(1)
//    void shouldCreateOrder() {
//
//        // Act
//        Orders result = orderServiceImpl.createOrder(orderDto);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(orderId, result.getOrderId());
//        assertEquals(customerId, result.getCustomerId());
//
//        Orders fetchedOrder = orderRepository.findById(result.getOrderId()).orElse(null);
//        assertNotNull(fetchedOrder);
//
//        System.out.println("New order id: " + orderId);
//        System.out.println("New order customer id: " + customerId);
//        System.out.println("New Order:" + result);
//    }

    @Test
    @Order(1)
    void shouldCreateOrder() {

        // Act
        Orders result = orderServiceImpl.createOrder(orderDto);

        // Assert
        assertNotNull(result);
        assertEquals(orderDto.getOrderId(), result.getOrderId());
        assertEquals(orderDto.getCustomerId(), result.getCustomerId());

        // Print results for debugging purposes
        System.out.println("Order id: " + orderId);
        System.out.println("Order customer id: " + customerId);
        System.out.println("Order:" + result);
    }

    @Test
    @Order(2)
    void shouldReturnByOrderId() {
        System.out.println("Order id: " + orderId);

        // Act
        Optional<Orders> result = orderServiceImpl.getByOrderId(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.get().getOrderId());
        assertEquals(customerId, result.get().getCustomerId());

        // Print the result to the console
        System.out.println("Order id: " + orderId);
        System.out.println("Order customer id: " + customerId);
        System.out.println("Order:" + result);
    }

    @Test
    @Order(3)
    void shouldReturnAllOrderDetailsByCustomerId() {

        // Act: Call the method
        List<Orders> result = orderServiceImpl.getAllOrderDetailsByCustomerId(customerId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderId, newOrder.getOrderId());
        assertEquals(customerId, newOrder.getCustomerId());

        // Print the result to the console
        System.out.println("Order id: " + orderId);
        System.out.println("Order customer id: " + customerId);
        System.out.println("Order:" + result);
    }

    @Test
    @Order(4)
    void shouldReturnOrderDetailsByCustomerId() {

        // Act: Call the method
        Optional<Orders> result = orderServiceImpl.getOrderDetailsByCustomerIdAndOrderId(customerId, orderId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(orderId, result.get().getOrderId());
        assertEquals(customerId, result.get().getCustomerId());

        // Print the result to the console
        System.out.println("Order id: " + orderId);
        System.out.println("Order customer id: " + customerId);
        System.out.println("Order:" + result);
    }

    @Test
    @Order(5)
    void shouldUpdateOrder() {

        totalAmount = BigDecimal.valueOf(500.50);
        orderStatus = "Shipped";
        shippingAddress = "Montalban Rizal";
        paymentMethod = "Cash on delivery";
        paymentStatus = "Paid";
        shippingMethod = "Standard Express";
        trackingNumber = orderServiceImpl.generateTrackingNumber();

        OrderDto updatedOrderDto = new OrderDto(
                orderId,
                customerId,
                totalAmount,
                orderStatus,
                shippingAddress,
                paymentMethod,
                paymentStatus,
                shippingMethod,
                trackingNumber
        );

        Orders updatedOrder = Orders.builder()
                .orderId(orderId)
                .customerId(customerId)
                .totalAmount(totalAmount)
                .orderStatus(orderStatus)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .shippingMethod(shippingMethod)
                .trackingNumber(trackingNumber)
                .build();

        // Call method
        Orders result = orderServiceImpl.updateOrder(customerId, orderId, updatedOrderDto);

        // Verify
        assertNotNull(result);
        assertEquals(orderId, updatedOrderDto.getOrderId());
        assertEquals(customerId, updatedOrderDto.getCustomerId());

        // Print the result to the console
        System.out.println("Order id: " + orderId);
        System.out.println("Order customer id: " + customerId);
        System.out.println("Order:" + result);
    }
}

package com.codewithkirk.OrderItemsService.UnitTesting;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import com.codewithkirk.OrderItemsService.Model.OrderItems;
import com.codewithkirk.OrderItemsService.Repository.OrderItemsRepository;
import com.codewithkirk.OrderItemsService.Service.impl.OrderItemsServiceImpl;
import com.codewithkirk.OrderItemsService.ServiceClient.Orders.OrderServiceClient;
import com.codewithkirk.OrderItemsService.ServiceClient.Products.ProductServiceClient;
import com.codewithkirk.OrderItemsService.ServiceClient.Users.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
public class OrderItemsUnitTest {

    @Mock
    private OrderItemsRepository orderItemsRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private OrderServiceClient orderServiceClient;

    @InjectMocks
    private OrderItemsServiceImpl orderItemsServiceImpl;

    private OrderItemsDto orderItemsDto;

    private OrderItems newOrderItems;

    private Long orderItemsId;
    private String orderId;
    private Long customerId;
    private Long productId;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @BeforeEach
    void Setup() {
        MockitoAnnotations.openMocks(this);

        orderItemsId = 1L;
        orderId = "2a6a44dd";
        customerId = 1L;
        productId = 1L;
        quantity = 2;
        unitPrice = BigDecimal.valueOf(100.50);
        totalPrice = BigDecimal.valueOf(201.00);

        orderItemsDto = new OrderItemsDto(
                orderItemsId,
                orderId,
                customerId,
                productId,
                quantity,
                unitPrice,
                totalPrice
        );

        newOrderItems = OrderItems.builder()
                .orderItemsId(orderItemsId)
                .orderId(orderId)
                .customerId(customerId)
                .productId(productId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
    }

    @Test
    @Order(1)
    void shouldcreateOrderItems() {
        // Stub the repository save method to return the saved seller
        when(userServiceClient.getUserById(customerId)).thenReturn(null); // Mock a valid user
        when(productServiceClient.showProductById(productId)).thenReturn(null);
        when(orderServiceClient.getOrderById(orderId)).thenReturn(null);
        when(orderItemsRepository.save(any(OrderItems.class))).thenReturn(newOrderItems);

        orderItemsServiceImpl.createOrderItems(orderItemsDto);
        assertEquals(orderId, orderItemsDto.getOrderId());
        assertEquals(customerId, orderItemsDto.getCustomerId());
        assertEquals(productId, orderItemsDto.getProductId());

        verify(orderItemsRepository, times(1)).save(any(OrderItems.class));

        System.out.println("New order items id: " + orderItemsId);
        System.out.println("New order items order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New order product id: " + productId);
        System.out.println("New order items: " + orderItemsDto);
    }

    @Test
    @Order(2)
    void shouldReturnAllOrderItemsDetailsByCustomerId() {

        List<OrderItems> orderItems = Arrays.asList(newOrderItems);

        // Mocking the repository method to return the mock products list
        when(orderItemsRepository.findByCustomerId(customerId)).thenReturn(orderItems);

        // Act: Call the method
        List<OrderItems> result = orderItemsServiceImpl.getAllOrderItemsDetailsByCustomerId(customerId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderId, newOrderItems.getOrderId());
        assertEquals(customerId, newOrderItems.getCustomerId());
        assertEquals(productId, newOrderItems.getProductId());

        // Verify that the repository method was called once
        verify(orderItemsRepository, times(1))
                .findByCustomerId(customerId);

        System.out.println("New order items id: " + orderItemsId);
        System.out.println("New order items order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New order product id: " + productId);
        System.out.println("New Order:" + result);
    }

    @Test
    @Order(3)
    void shouldReturnOrderItemsById() {
        when(orderItemsRepository.findById(orderItemsId))
                .thenReturn(Optional.of(newOrderItems));

        // Act
        Optional<OrderItems> result = orderItemsServiceImpl
                .getOrderItemsById(orderItemsId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getOrderId());
        assertEquals(customerId, result.get().getCustomerId());
        assertEquals(productId, result.get().getProductId());

        // Verify interactions
        verify(orderItemsRepository, times(1))
                .findById(orderItemsId);

        // Print the result to the console
        System.out.println("New order items id: " + orderItemsId);
        System.out.println("New order items order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New order product id: " + productId);
        System.out.println("New Order:" + result);
    }
}

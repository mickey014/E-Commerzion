package com.codewithkirk.orderService.UnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.codewithkirk.orderService.Dto.OrderDto;
import com.codewithkirk.orderService.Repository.OrderRepository;
import com.codewithkirk.orderService.Model.Orders;
import com.codewithkirk.orderService.Service.impl.OrderServiceImpl;
import com.codewithkirk.orderService.ServiceClient.Products.ProductServiceClient;
import com.codewithkirk.orderService.ServiceClient.Users.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private OrderServiceUnitTest orderServiceUnitTest;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private OrderDto orderDto;
    private Orders newOrder;

    private String orderId;
    private Long customerId = 1L;
    private Long productId = 1L;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingMethod;
    private String trackingNumber;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderId = UUID.randomUUID().toString().substring(0,8);
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
                productId,
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
                .productId(productId)
                .totalAmount(totalAmount)
                .orderStatus(orderStatus)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .shippingMethod(shippingMethod)
                .trackingNumber(trackingNumber)
                .build();
    }

    @Test
    @Order(1)
    void shouldCreateOrder() {

        // Mock SellerServiceClient behavior
        when(userServiceClient.getUserById(customerId)).thenReturn(null);
        //when(productServiceClient.showProductById()).thenReturn(null);

        // Stub the repository save method to return the saved seller
        when(orderRepository.save(any(Orders.class))).thenReturn(newOrder);

        orderServiceImpl.createOrder(orderDto);

        // Assert
        assertEquals(orderId, orderDto.getOrderId());
        assertEquals(customerId, orderDto.getCustomerId());
        verify(orderRepository, times(1)).save(any(Orders.class));

        System.out.println("New order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New Order:" + orderDto);
    }

    @Test
    @Order(2)
    void shouldReturnByOrderId() {
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(newOrder));

        // Act
        Optional<Orders> result = orderServiceImpl
                .getByOrderId(orderId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getOrderId());
        assertEquals(customerId, result.get().getCustomerId());

        // Verify interactions
        verify(orderRepository, times(1))
                .findById(orderId);

        // Print the result to the console
        System.out.println("Order id: " + orderId);
        System.out.println("Order: " + result);
    }

    @Test
    @Order(3)
    void shouldReturnAllOrderDetailsByCustomerId() {
        totalAmount = BigDecimal.valueOf(500.50);
        orderStatus = "Shipped";
        shippingAddress = "Montalban Rizal";
        paymentMethod = "Cash on delivery";
        paymentStatus = "Paid";
        shippingMethod = "Standard Express";
        trackingNumber = orderServiceImpl.generateTrackingNumber();

        Orders newOrder2 = Orders.builder()
                .orderId(orderId)
                .customerId(customerId)
                .productId(productId)
                .totalAmount(totalAmount)
                .orderStatus(orderStatus)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .shippingMethod(shippingMethod)
                .trackingNumber(trackingNumber)
                .build();

        List<Orders> orders = Arrays.asList(newOrder, newOrder2);

        // Mocking the repository method to return the mock products list
        when(orderRepository.findByCustomerId(customerId)).thenReturn(orders);

        // Act: Call the method
        List<Orders> result = orderServiceImpl.getAllOrderDetailsByCustomerId(customerId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(orderId, newOrder.getOrderId());
        assertEquals(customerId, newOrder.getCustomerId());

        // Verify that the repository method was called once
        verify(orderRepository, times(1))
                .findByCustomerId(customerId);

        // Print the result to the console
        System.out.println("New order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New Order:" + result);
    }

    @Test
    @Order(4)
    void shouldReturnOrderDetailsByCustomerId() {
        // Mocking the repository to return the product when searched by sellerId and productId
        when(orderRepository.findByCustomerIdAndOrderId(customerId, orderId))
                .thenReturn(Optional.of(newOrder));

        // Act: Call the method
        Optional<Orders> result = orderServiceImpl.getOrderDetailsByCustomerIdAndOrderId(customerId, orderId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(orderId, result.get().getOrderId());
        assertEquals(customerId, result.get().getCustomerId());

        // Verify that the repository method was called once with the correct arguments
        verify(orderRepository, times(1))
                .findByCustomerIdAndOrderId(customerId, orderId);

        // Print the result to the console
        System.out.println("New order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New Order:" + result);
    }

    @Test
    @Order(5)
    void shouldUpdateOrder() {
        // Mock a return value (adjust as needed)

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
                productId,
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
                .productId(productId)
                .totalAmount(totalAmount)
                .orderStatus(orderStatus)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .shippingMethod(shippingMethod)
                .trackingNumber(trackingNumber)
                .build();

        when(userServiceClient.getUserById(customerId)).thenReturn(null); // Mock a valid user
        when(orderRepository.findByCustomerIdAndOrderId(customerId, orderId))
                .thenReturn(Optional.of(updatedOrder));
        when(orderRepository.save(any(Orders.class))).thenReturn(updatedOrder);

        // Call method
        Orders result = orderServiceImpl.updateOrder(customerId, orderId, updatedOrderDto);

        // Verify
        assertNotNull(result);
        assertEquals(orderId, updatedOrderDto.getOrderId());
        assertEquals(customerId, updatedOrderDto.getCustomerId());

        verify(orderRepository, times(1)).save(any(Orders.class));

        // Print the result to the console
        System.out.println("New order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New Order:" + result);
    }
}

package com.codewithkirk.OrderItemsService.IntegrationTesting;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import com.codewithkirk.OrderItemsService.IntegrationTesting.Config.OrderItemsServiceIntegrationTestConfig;
import com.codewithkirk.OrderItemsService.Model.OrderItems;
import com.codewithkirk.OrderItemsService.Repository.OrderItemsRepository;
import com.codewithkirk.OrderItemsService.Service.impl.OrderItemsServiceImpl;
import com.codewithkirk.OrderItemsService.ServiceClient.Orders.OrderServiceClient;
import com.codewithkirk.OrderItemsService.ServiceClient.Products.ProductServiceClient;
import com.codewithkirk.OrderItemsService.ServiceClient.Users.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class OrderItemsServiceIntegrationTest extends OrderItemsServiceIntegrationTestConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private OrderItemsServiceImpl orderItemsServiceImpl;
    
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(OrderItemsServiceIntegrationTest.class);

    private OrderItemsDto orderItemsDto;

    private OrderItems newOrderItems;

    private Long orderItemsId = 1L;
    private String orderId;
    private Long customerId = 1L;
    private Long productId = 1L;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @BeforeEach
    void setUp() {
        orderId = "4a202918";
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
                .orderId(orderItemsDto.getOrderId())
                .customerId(orderItemsDto.getCustomerId())
                .productId(orderItemsDto.getProductId())
                .quantity(orderItemsDto.getQuantity())
                .unitPrice(orderItemsDto.getUnitPrice())
                .totalPrice(orderItemsDto.getUnitPrice().multiply(new BigDecimal(orderItemsDto.getQuantity())))
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

    //@Test
    //@Order(1)
    void shouldcreateOrderItems() {
        // userServiceClient.getUserById(customerId);
        // productServiceClient.showProductById(productId);
        // orderServiceClient.getOrderById(orderId);

        orderItemsServiceImpl.createOrderItems(orderItemsDto);

        assertEquals(orderId, orderItemsDto.getOrderId());
        assertEquals(customerId, orderItemsDto.getCustomerId());
        assertEquals(productId, orderItemsDto.getProductId());


        System.out.println("New order items id: " + orderItemsId);
        System.out.println("New order items order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New order product id: " + productId);
        System.out.println("New order items: " + orderItemsDto);
    }

    //@Test
    //@Order(2)
    void shouldReturnAllOrderItemsDetailsByCustomerId() {

        List<OrderItems> orderItems = Arrays.asList(newOrderItems);
        logger.info("Received order with ID: {}", orderItemsDto.getOrderId());

        
        System.out.println(orderItemsId);
        System.out.println(orderId);

        // Act: Call the method
        List<OrderItems> result = orderItemsServiceImpl.getAllOrderItemsDetailsByCustomerId(customerId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderId, newOrderItems.getOrderId());
        assertEquals(customerId, newOrderItems.getCustomerId());
        assertEquals(productId, newOrderItems.getProductId());

        System.out.println("New order items id: " + orderItemsId);
        System.out.println("New order items order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New order product id: " + productId);
        System.out.println("New Order:" + result);
    }

    @Test
    @Order(1)
    void shouldReturnOrderItemsById() {

        // Act
        Optional<OrderItems> result = orderItemsServiceImpl
                .getOrderItemsById(orderItemsDto.getOrderItemsId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getOrderId());
        assertEquals(customerId, result.get().getCustomerId());
        assertEquals(productId, result.get().getProductId());

        // Print the result to the console
        System.out.println("New order items id: " + orderItemsId);
        System.out.println("New order items order id: " + orderId);
        System.out.println("New order customer id: " + customerId);
        System.out.println("New order product id: " + productId);
        System.out.println("New Order:" + result);
    }

}

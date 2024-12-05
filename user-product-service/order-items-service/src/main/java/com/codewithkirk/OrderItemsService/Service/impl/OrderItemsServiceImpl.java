package com.codewithkirk.OrderItemsService.Service.impl;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import com.codewithkirk.OrderItemsService.Exception.OrderItemsException;
import com.codewithkirk.OrderItemsService.Model.OrderItems;
import com.codewithkirk.OrderItemsService.Repository.OrderItemsRepository;
import com.codewithkirk.OrderItemsService.Service.OrderItemsService;
import com.codewithkirk.OrderItemsService.ServiceClient.Orders.OrderServiceClient;
import com.codewithkirk.OrderItemsService.ServiceClient.Products.ProductServiceClient;
import com.codewithkirk.OrderItemsService.ServiceClient.Users.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.codewithkirk.OrderItemsService.Configuration.RabbitConfig.ORDER_ITEMS_QUEUE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemsServiceImpl implements OrderItemsService {

    private final OrderItemsRepository orderItemsRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderServiceClient orderServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(OrderItemsServiceImpl.class);

    @RabbitListener(queues = ORDER_ITEMS_QUEUE)
    @Override
    public OrderItems createOrderItems(OrderItemsDto orderItemsDto) {

        logger.info("Received order with ID: {}", orderItemsDto.getOrderId());
        String orderId = orderItemsDto.getOrderId();
        Long customerId = orderItemsDto.getCustomerId();
        Long productId = orderItemsDto.getProductId();
        Integer quantity = orderItemsDto.getQuantity();
        BigDecimal unitPrice = orderItemsDto.getUnitPrice();
        BigDecimal totalPrice = orderItemsDto.getUnitPrice().multiply(new BigDecimal(orderItemsDto.getQuantity()));

        userServiceClient.getUserById(customerId);
        productServiceClient.showProductById(productId);

        OrderItems orderItems = OrderItems.builder()
                .orderId(orderId)
                .customerId(customerId)
                .productId(productId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();

        orderItemsRepository.save(orderItems);
        orderServiceClient.getOrderById(orderId);
        return orderItems;
    }


    @Override
    public List<OrderItems> getAllOrderItemsDetailsByCustomerId(Long customerId) {
        List<OrderItems> orderItems = orderItemsRepository.findByCustomerId(customerId);

        if (orderItems.isEmpty()) {
            throw new OrderItemsException("No OrderItems found for this customer.");
        }

        return orderItems;
    }

    @Override
    public Optional<OrderItems> getOrderItemsById(Long orderItemsId) {
        return Optional.ofNullable(orderItemsRepository.findById(orderItemsId)
                .orElseThrow(() -> new OrderItemsException("No orders found .")));
    }
}

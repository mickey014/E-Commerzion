package com.codewithkirk.orderService.Service.impl;

import com.codewithkirk.orderService.Dto.*;
import com.codewithkirk.orderService.Exception.OrderException;
import com.codewithkirk.orderService.Model.Orders;
import com.codewithkirk.orderService.Repository.OrderRepository;
import com.codewithkirk.orderService.Service.OrderService;
import com.codewithkirk.orderService.ServiceClient.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.codewithkirk.orderService.Configuration.RabbitConfig.ORDER_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @RabbitListener(queues = ORDER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @Override
    public void createOrder(OrderDto orderDto) {

        // use this for testing
        String orderId = UUID.randomUUID().toString().substring(0,8);

        try {
            logger.info("Received order with ID: {}", orderDto.getOrderId());

            Long customerId = orderDto.getCustomerId();
            BigDecimal totalAmount = orderDto.getTotalAmount();
            String orderStatus = orderDto.getOrderStatus().trim();
            String shippingAddress = orderDto.getShippingAddress().replaceAll("\\s+", " ").trim();
            String paymentMethod = orderDto.getPaymentMethod().trim();
            String paymentStatus = orderDto.getPaymentStatus().trim();
            String shippingMethod = orderDto.getShippingMethod().trim();
            String trackingNumber = generateTrackingNumber().replaceAll("-", "").trim();

            if (shippingAddress.isEmpty()) {
                throw new OrderException("Shipping address is required.");
            }

            Orders order = Orders.builder()
                    .orderId(orderDto.getOrderId())
                    .customerId(customerId)
                    .totalAmount(totalAmount)
                    .orderStatus(orderStatus)
                    .shippingAddress(shippingAddress)
                    .paymentMethod(paymentMethod)
                    .paymentStatus(paymentStatus)
                    .shippingMethod(shippingMethod)
                    .trackingNumber(trackingNumber)
                    .build();

            orderRepository.save(order);
            logger.info("Order with ID {} saved successfully", orderDto.getOrderId());
        } catch (OrderException e) {
            logger.error("Order exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while processing order", e);
            throw new OrderException("Unexpected error occurred while creating order: " + e.getMessage());
        }
    }

    @Override
    public Optional<Orders> getByOrderId(String orderId) {
        return Optional.ofNullable(orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("No orders found.")));
    }


    @Override
    public List<Orders> getAllOrderDetailsByCustomerId(Long customerId) {
        List<Orders> orders = orderRepository.findByCustomerId(customerId);
        if (orders.isEmpty()) {
            throw new OrderException("No orders found for this customer.");
        }
        return orders;
    }

    @Override
    public Optional<Orders> getOrderDetailsByCustomerIdAndOrderId(Long customerId, String orderId) {
        return Optional.ofNullable(orderRepository.findByCustomerIdAndOrderId(customerId, orderId)
                .orElseThrow(() -> new OrderException("No orders found for this customer.")));
    }

    @Override
    public Orders updateOrder(Long customerId, String orderId, OrderDto orderDto) {
        userServiceClient.getUserById(customerId);
        String orderStatus = orderDto.getOrderStatus().trim();
        String shippingAddress = orderDto.getShippingAddress().replaceAll("\\s+", " ").trim();
        String paymentMethod = orderDto.getPaymentMethod();
        String paymentStatus = orderDto.getPaymentStatus();

        // Fetch the order from the repository by ID
        Orders order = orderRepository.findByCustomerIdAndOrderId(customerId, orderId)
                .orElseThrow(() -> new OrderException("No orders found for this customer."));

        if(shippingAddress.isEmpty()) {
            throw new OrderException("Shipping address is required.");
        }

        order.setOrderStatus(orderStatus);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(paymentStatus);

        orderRepository.save(order);
        return order;
    }


    public String generateTrackingNumber() {
        // Get the current date and time in the format yyyy-MM-dd HH:mm:ss
        String currentDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Generate a unique tracking number using UUID
        String uuid = UUID.randomUUID().toString().toUpperCase();

        // Combine the current date, time, and UUID to create a tracking number
        String trackingNumber = currentDateTime + uuid;

        return trackingNumber;
    }
}

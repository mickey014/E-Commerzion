package com.codewithkirk.OrderItemsService.Configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ORDER_QUEUE = "orderQueue";
    public static final String ORDER_ITEMS_QUEUE = "orderItemsQueue";
    public static final String INVENTORY_QUEUE = "inventoryQueue";

    public static final String ORDER_EXCHANGE = "orderExchange";
    public static final String ORDER_ITEMS_EXCHANGE = "orderItemsExchange";
    public static final String INVENTORY_EXCHANGE = "inventoryExchange";

    public static final String ORDER_ROUTING_KEY = "orderRoutingKey";
    public static final String ORDER_ITEMS_ROUTING_KEY = "orderRoutingKey";
    public static final String INVENTORY_ROUTING_KEY = "inventoryRoutingKey";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Queues
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true); // Durable queue for orders
    }

    @Bean
    public Queue orderItemsQueue() {
        return new Queue(ORDER_ITEMS_QUEUE, true); // Durable queue for order items
    }

    @Bean
    public Queue inventoryQueue() {
        return new Queue(INVENTORY_QUEUE, true); // Durable queue for order items
    }

    // Exchange
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public DirectExchange orderItemsExchange() {
        return new DirectExchange(ORDER_ITEMS_EXCHANGE);
    }

    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange(INVENTORY_EXCHANGE);
    }

    // Bindings
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding orderItemsBinding() {
        return BindingBuilder.bind(orderItemsQueue()).to(orderItemsExchange()).with(ORDER_ITEMS_ROUTING_KEY);
    }

    @Bean
    public Binding inventoryBinding() {
        return BindingBuilder.bind(inventoryQueue()).to(inventoryExchange()).with(INVENTORY_ROUTING_KEY);
    }
}

package com.codewithkirk.productService.Service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.codewithkirk.productService.Configuration.RabbitConfig.ORDER_EXCHANGE;
import static com.codewithkirk.productService.Configuration.RabbitConfig.ORDER_ITEMS_EXCHANGE;
import static com.codewithkirk.productService.Configuration.RabbitConfig.ORDER_ROUTING_KEY;
import static com.codewithkirk.productService.Configuration.RabbitConfig.ORDER_ITEMS_ROUTING_KEY;

import com.codewithkirk.productService.Dto.*;
import com.codewithkirk.productService.Exception.ProductOrderServiceUnavailableException;
import com.codewithkirk.productService.ServiceClient.OrderItems.OrderItemsServiceClient;
import com.codewithkirk.productService.ServiceClient.Orders.OrderServiceClient;
import com.codewithkirk.productService.ServiceClient.Sellers.SellerServiceClient;
import com.codewithkirk.productService.ServiceClient.Users.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.codewithkirk.productService.Exception.ProductException;
import com.codewithkirk.productService.Model.Products;
import com.codewithkirk.productService.Repository.ProductRepository;
import com.codewithkirk.productService.Service.ProductService;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService{
    
    private final ProductRepository productRepository;

    private final UserServiceClient userServiceClient;
    private final SellerServiceClient sellerServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final OrderItemsServiceClient orderItemsServiceClient;

    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImp.class);


    @Override
    public Products newProduct(ProductDto productDto) {
        sellerServiceClient.getSellerByUserIdAndSellerId(productDto.getUserId(),
                                                        productDto.getSellerId());

        // Replace multiple spaces with one space
        Long sellerId = productDto.getSellerId();
        Long userId = productDto.getUserId();
        String productName = productDto.getProductName().replaceAll("\\s+", " ").trim();
        String skuCode = productDto.getSkuCode().replaceAll("\\s+", " ").trim();
        String productDescription = productDto.getProductDescription().replaceAll("\\s+", " ").trim();
        String productCategory = productDto.getProductCategory().replaceAll("\\s+", " ").trim();
        BigDecimal price = productDto.getPrice();
        String features = productDto.getFeatures().replaceAll("\\s+", " ").trim();
        Integer stock = productDto.getStock();
        String availability = productDto.getAvailability().replaceAll("\\s+", " ").trim();
        String photoUrl = productDto.getPhotoUrl();

        if(productName.isEmpty()) {
            throw new ProductException("Product Name is required.");
        }

        if(productDescription.isEmpty()) {
            throw new ProductException("Description is required.");
        }

        if(productDescription.length() <= 5) {
            throw new ProductException("Description must be atleast 5 characters long.");
        }

        if(productCategory.isEmpty()) {
            throw new ProductException("Category is required.");
        }


        Products newProduct = Products.builder()
            .sellerId(sellerId)
            .userId(userId)
            .productName(productName)
            .skuCode(skuCode)
            .productDescription(productDescription)
            .productCategory(productCategory)
            .price(price)
            .features(features)
            .stock(stock)
            .availability(availability)
            .photoUrl(photoUrl)
            .build();

        return productRepository.save(newProduct);
    }

    @Override
    public Products productPurchase(ProductPurchaseDto productPurchaseDto) {
        userServiceClient.getUserById(productPurchaseDto.getCustomerId());
        if(!isOrderServiceUp()) {
            throw new ProductOrderServiceUnavailableException("Order Service is down. Order processing aborted.");
        } else if(!isOrderItemsServiceUp()) {
            throw new ProductOrderServiceUnavailableException("Order Items Service is down. Order processing aborted.");
        }

        String orderId = UUID.randomUUID().toString().substring(0, 8);

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(orderId);
        orderDto.setCustomerId(productPurchaseDto.getCustomerId());
        orderDto.setOrderStatus("Pending");
        orderDto.setPaymentMethod(productPurchaseDto.getPaymentMethod());
        orderDto.setPaymentStatus(productPurchaseDto.getPaymentStatus());
        orderDto.setShippingAddress(productPurchaseDto.getShippingAddress());
        orderDto.setShippingMethod(productPurchaseDto.getShippingMethod());
        orderDto.setTrackingNumber(generateTrackingNumber());



        List<OrderItemsDto> orderItems = convertToOrderItems(productPurchaseDto.getOrderItems(), orderId, orderDto.getCustomerId());
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }

        orderDto.setTotalAmount(calculateTotalAmount(orderItems));
        orderDto.setOrderItems(orderItems);

        try {
            // Send order to RabbitMQ first with orderId
            rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ROUTING_KEY, orderDto);
            logger.info("Order sent to RabbitMQ with id: {}", orderId);

            // Send each order item to RabbitMQ, including the orderId
            for (OrderItemsDto orderItem : orderItems) {
                // Ensure orderId is set for each order item (in case it's not in the conversion)
                orderItem.setOrderId(orderId);

                rabbitTemplate.convertAndSend(ORDER_ITEMS_EXCHANGE, ORDER_ITEMS_ROUTING_KEY, orderItem);
                logger.info("Order item sent to RabbitMQ: {}", orderItem);
            }

            // Update product stock after processing order items
            updateProductStock(productPurchaseDto.getOrderItems());
            logger.info("Product stock updated successfully");

        } catch (Exception e) {
            logger.error("Error occurred while sending messages to RabbitMQ", e);
            // Optionally, handle retry logic or alert the system
        }
        return null;
    }

    public void updateProductStock(List<ProductItemsDto> orderItems) {
        for (ProductItemsDto item : orderItems) {
            // Find the product from the database based on the productId
            Optional<Products> productOptional = productRepository.findById(item.getProductId());

            if (productOptional.isPresent()) {
                Products product = productOptional.get();

                // Check if the stock is sufficient
                if (product.getStock() >= item.getQuantity()) {
                    // Deduct the ordered quantity from the product's stock
                    product.setStock(product.getStock() - item.getQuantity());

                    // Save the updated product back to the database
                    productRepository.save(product);
                } else {
                    // Handle case where there isn't enough stock (e.g., throw an exception)
                    throw new ProductException("Not enough stock for product ID: " + item.getProductId());
                }
            } else {
                // Handle case where the product doesn't exist (e.g., throw an exception)
                throw new ProductException("Product not found with ID: " + item.getProductId());
            }
        }
    }


    @Override
    public List<ProductDto> showProducts() {

        List<Products> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductDto(
                        product.getProductId(),
                        product.getSellerId(),
                        product.getUserId(),
                        product.getProductName(),
                        product.getSkuCode(),
                        product.getProductDescription(),
                        product.getProductCategory(),
                        product.getPrice(),
                        product.getFeatures(),
                        product.getStock(),
                        product.getAvailability(),
                        product.getPhotoUrl()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Products> showProductBySellerIdAndProductId(Long sellerId, Long productId) {
        return Optional.ofNullable(productRepository.findProductBySellerIdAndProductId(sellerId, productId)
                .orElseThrow(() -> new ProductException("Product does not exists.")));
    }

    @Override
    public Optional<Products> showProductById(Long productId) {
        return Optional.ofNullable(productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product does not exists.")));
    }

    @Override
    public List<Products> showAllProductsBySellerId(Long sellerId) {
        List<Products> products = productRepository.findAllProductsBySellerId(sellerId);
        if (products.isEmpty()) {
            throw new ProductException("No orders found for this customer.");
        }

        return products;
    }

    @Override
    public Products updateProduct(Long userId, Long sellerId,
                                  Long productId, ProductDto productDto) {
        sellerServiceClient.getSellerByUserIdAndSellerId(userId, sellerId);
        // Retrieve the user by ID (Assuming user exists)
        Products product = productRepository.findProductBySellerIdAndProductId(sellerId, productId)
                .orElseThrow(() -> new ProductException("Product does not exists."));

        // Replace multiple spaces with one space
        String productName = productDto.getProductName().replaceAll("\\s+", " ").trim();
        String skuCode = productDto.getSkuCode().replaceAll("\\s+", " ").trim();
        String productDescription = productDto.getProductDescription().replaceAll("\\s+", " ").trim();
        String productCategory = productDto.getProductCategory().replaceAll("\\s+", " ").trim();
        BigDecimal price = productDto.getPrice();
        String features = productDto.getFeatures().replaceAll("\\s+", " ").trim();
        Integer stock = productDto.getStock();
        String availability = productDto.getAvailability().replaceAll("\\s+", " ").trim();
        String photoUrl = productDto.getPhotoUrl();

        if(productName.isEmpty()) {
            throw new ProductException("Product Name is required.");
        }

        if(productDescription.isEmpty()) {
            throw new ProductException("Description is required.");
        }

        if(productDescription.length() <= 5) {
            throw new ProductException("Description must be atleast 5 characters long.");
        }

        if(productCategory.isEmpty()) {
            throw new ProductException("Category is required.");
        }

        product.setProductName(productName);
        product.setSkuCode(skuCode);
        product.setProductDescription(productDescription);
        product.setProductCategory(productCategory);
        product.setPrice(price);
        product.setFeatures(features);
        product.setStock(stock);
        product.setAvailability(availability);
        product.setPhotoUrl(photoUrl);

        return productRepository.save(product);
    }


    @Override
    public void forceDeleteProduct(Long userId, Long sellerId, Long productId) {
        sellerServiceClient.getSellerByUserIdAndSellerId(userId, sellerId);
        Products existingProducts  = productRepository.findProductBySellerIdAndProductId(
                                                        sellerId, productId)
                .orElseThrow(() -> new ProductException("Product does not exists."));
        productRepository.delete(existingProducts);
    }

    private BigDecimal calculateTotalAmount(List<OrderItemsDto> productItems) {
        // Sum the total amount from the price * quantity of each product
        return productItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItemsDto> convertToOrderItems(List<ProductItemsDto> productItems,
                                                    String orderId,
                                                    Long customerId) {
        List<OrderItemsDto> orderItems = new ArrayList<>();
        for (ProductItemsDto productItem : productItems) {
            OrderItemsDto orderItem = new OrderItemsDto();
            orderItem.setOrderId(orderId);
            orderItem.setCustomerId(customerId);
            orderItem.setProductId(productItem.getProductId());
            orderItem.setQuantity(productItem.getQuantity());
            orderItem.setUnitPrice(productItem.getUnitPrice());
            orderItem.setTotalPrice(productItem.getUnitPrice().multiply(BigDecimal.valueOf(productItem.getQuantity())));
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private String generateTrackingNumber() {
        // Get the current date and time in the format yyyy-MM-dd HH:mm:ss
        String currentDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Generate a unique tracking number using UUID
        String uuid = UUID.randomUUID().toString().toUpperCase();

        // Combine the current date, time, and UUID to create a tracking number
        String trackingNumber = currentDateTime + uuid;

        return trackingNumber;
    }

    public boolean isOrderServiceUp() {
        try {
            String healthStatus = orderServiceClient.checkHealth();
            // Assuming the response JSON contains {"status": "UP"}
            return healthStatus.contains("\"status\":\"UP\"");  // Checks if the target service is UP
        } catch (Exception e) {
            // Log the exception and return false if the service is down or unreachable
            return false;
        }
    }

    @Override
    public boolean isOrderItemsServiceUp() {
        try {
            String healthStatus = orderItemsServiceClient.checkHealth();
            // Assuming the response JSON contains {"status": "UP"}
            return healthStatus.contains("\"status\":\"UP\"");  // Checks if the target service is UP
        } catch (Exception e) {
            // Log the exception and return false if the service is down or unreachable
            return false;
        }
    }

}

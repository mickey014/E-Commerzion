package com.codewithkirk.productService.IntegrationTesting;

import com.codewithkirk.productService.Dto.*;
import com.codewithkirk.productService.Exception.ProductOrderServiceUnavailableException;
import com.codewithkirk.productService.IntegrationTesting.Config.ProductServiceIntegrationTestConfig;
import com.codewithkirk.productService.Model.Products;
import com.codewithkirk.productService.Repository.ProductRepository;
import com.codewithkirk.productService.Service.impl.ProductServiceImp;
import com.codewithkirk.productService.ServiceClient.OrderItems.OrderItemsServiceClient;
import com.codewithkirk.productService.ServiceClient.Orders.OrderServiceClient;
import com.codewithkirk.productService.ServiceClient.Sellers.SellerServiceClient;
import com.codewithkirk.productService.ServiceClient.Users.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
public class ProductServiceIntegrationTest extends ProductServiceIntegrationTestConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private SellerServiceClient sellerServiceClient;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private OrderItemsServiceClient orderItemsServiceClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProductServiceImp productServiceImp;

    private Long userId = 2L;
    private Long sellerId = 1L;
    private Long productId = 1L;

    private ProductDto productDto;
    private Products newProduct;

    private String productName;
    private String skuCode;
    private String productDescription;
    private String productCategory;
    private BigDecimal price;
    private String features;
    private Integer stock;
    private String availability;
    private String photoUrl;

    @BeforeEach
    void setUp() {
        // Arrange reusable test data
        productName = "Iphone 13";
        skuCode = "20241204TES213EWFQ";
        productDescription = "Iphone 13 Pro Max";
        productCategory = "Mobile Phones";
        price = BigDecimal.valueOf(999.50);
        features = "Folded Phone and Fast Charging";
        stock = 500;
        availability = "In Stock";
        photoUrl = "http://example.com/iphone13.jpg";

        productDto = new ProductDto(
                productId,
                sellerId,
                userId,
                productName,
                skuCode,
                productDescription,
                productCategory,
                price,
                features,
                stock,
                availability,
                photoUrl
        );

        newProduct = Products.builder()
                .productId(productId)
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

    @Test
    @Order(1)
    void shouldCreateNewProduct() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        sellerServiceClient.getSellerByUserIdAndSellerId(productDto.getUserId(),
                productDto.getSellerId());
        Products result = productServiceImp.newProduct(productDto);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(userId, result.getUserId());
        assertEquals(sellerId, result.getSellerId());
        assertEquals(productName, result.getProductName());
        assertEquals(skuCode, result.getSkuCode());


        if(result != null) {
            System.out.println("Registered seller id: " + sellerId);
            System.out.println("Registered user id: " + userId);
            System.out.println("Registered seller: " + result);
        }
    }

    @Test
    @Order(2)
    void shouldPurchaseProduct() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        String orderId = UUID.randomUUID().toString().substring(0, 8);

        // Arrange: Set up the ProductPurchaseDto
        ProductPurchaseDto productPurchaseDto = new ProductPurchaseDto();
        productPurchaseDto.setOrderId(orderId);
        productPurchaseDto.setCustomerId(userId);
        productPurchaseDto.setProductId(productId);
        productPurchaseDto.setOrderStatus("Pending");
        productPurchaseDto.setShippingAddress("1234 Elm Street, Some City, Some Country");
        productPurchaseDto.setPaymentMethod("Credit Card");
        productPurchaseDto.setPaymentStatus("Paid");
        productPurchaseDto.setShippingMethod("Standard");

        userServiceClient.getUserById(productPurchaseDto.getCustomerId());

        List<ProductItemsDto> orderItems = new ArrayList<>();
        // Adding multiple ProductItemsDto objects to the list
        orderItems.add(new ProductItemsDto(productId, 1, BigDecimal.valueOf(50.25)));
        orderItems.add(new ProductItemsDto(productId, 1, BigDecimal.valueOf(50.25)));
        productPurchaseDto.setOrderItems(orderItems);

        productServiceImp.productPurchase(productPurchaseDto);

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product order id: " + orderId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println("Product Purchase: " + productPurchaseDto);
    }

    @Test
    @Order(3)
    void shouldReturnAllProducts() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        productName = "Iphone 14";
        skuCode = "2024120414TES213EWFQ";
        productDescription = "Iphone 14 Pro Max";
        productCategory = "Mobile Phones";
        price = BigDecimal.valueOf(1199.50);
        features = "Folded Phone and Fast Charging";
        stock = 500;
        availability = "In Stock";
        photoUrl = "http://example.com/iphon14.jpg";

        Long productId2 = 2L;
        Long sellerId2 = 2L;
        Long userId2 = 2L;

        Products newProduct2  = Products.builder()
                .productId(productId2)
                .sellerId(sellerId2)
                .userId(userId2)
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

        productRepository.saveAll(Arrays.asList(newProduct, newProduct2));

        // Act: Call the method
        List<ProductDto> result = productServiceImp.showProducts();

        // Assert: Verify the result
        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals(userId, newProduct.getUserId());
        assertEquals(sellerId, newProduct.getSellerId());
        assertEquals(productId, newProduct.getProductId());

        assertEquals(userId2, newProduct2.getUserId());
        assertEquals(sellerId2, newProduct2.getSellerId());
        assertEquals(productId2, newProduct2.getProductId());

        System.out.println("Products: " + result);
    }

    @Test
    @Order(4)
    void shouldReturnAllProductsBySellerId() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        productName = "Iphone 14";
        skuCode = "2024120414TES213EWFQ";
        productDescription = "Iphone 14 Pro Max";
        productCategory = "Mobile Phones";
        price = BigDecimal.valueOf(1199.50);
        features = "Folded Phone and Fast Charging";
        stock = 500;
        availability = "In Stock";
        photoUrl = "http://example.com/iphon14.jpg";

        Long productId2 = 2L;
        Long sellerId2 = 2L;
        Long userId2 = 2L;

        Products newProduct2  = Products.builder()
                .productId(productId2)
                .sellerId(sellerId2)
                .userId(userId2)
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

        productRepository.saveAll(Arrays.asList(newProduct, newProduct2));

        // Act: Call the method
        List<ProductDto> result = productServiceImp.showProducts();

        // Assert: Verify the result
        assertNotNull(result);

        assertEquals(2, result.size());
        assertEquals(userId, newProduct.getUserId());
        assertEquals(sellerId, newProduct.getSellerId());
        assertEquals(productId, newProduct.getProductId());

        assertEquals(userId2, newProduct2.getUserId());
        assertEquals(sellerId2, newProduct2.getSellerId());
        assertEquals(productId2, newProduct2.getProductId());

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println();
        System.out.println("Product id: " + productId2);
        System.out.println("Product user id: " + userId2);
        System.out.println("Product seller id: " + sellerId2);
        System.out.println();
        System.out.println("Products: " + result);
    }

    @Test
    @Order(5)
    void shouldReturnProductById() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        // Act: Call the method
        Optional<Products> result = productServiceImp.showProductById(productId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(productId, result.get().getProductId());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println("Product: " + result);
    }

    @Test
    @Order(6)
    void shouldReturnProductBySellerIdAndProductId() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        // Act: Call the method
        Optional<Products> result = productServiceImp.showProductBySellerIdAndProductId(sellerId, productId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(productId, result.get().getProductId());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println("Product: " + result);
    }

    @Test
    @Order(7)
    void shouldUpdateProduct() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        sellerServiceClient.getSellerByUserIdAndSellerId(productDto.getUserId(),
                productDto.getSellerId());

        Long productId = 1L;
        Long sellerId = 1L;
        Long userId = 1L;

        productName = "Iphone 14";
        skuCode = "2024120414TES213EWFQ";
        productDescription = "Iphone 14 Pro Max";
        productCategory = "Mobile Phones";
        price = BigDecimal.valueOf(1199.50);
        features = "Folded Phone and Fast Charging";
        stock = 300;
        availability = "In Stock";
        photoUrl = "http://example.com/iphon14.jpg";

        ProductDto updateProductDto = new ProductDto(
                productId,
                sellerId,
                userId,
                productName,
                skuCode,
                productDescription,
                productCategory,
                price,
                features,
                stock,
                availability,
                photoUrl
        );

        Products updateProduct  = Products.builder()
                .productId(productId)
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

        // Call method
        Products result = productServiceImp.updateProduct(userId, sellerId,
                productId, updateProductDto);

        // Verify
        assertNotNull(result);
        assertEquals(productId, updateProductDto.getProductId());
        assertEquals(sellerId, updateProductDto.getSellerId());
        assertEquals(userId, updateProductDto.getUserId());

        System.out.println("Updated product id: " + productId);
        System.out.println("Updated product seller id: " + sellerId);
        System.out.println("Updated product user id: " + userId);
        System.out.println("Updated product: " + result);
    }

    @Test
    @Order(8)
    void shouldForceDeleteProduct() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        sellerServiceClient.getSellerByUserIdAndSellerId(productDto.getUserId(),
                productDto.getSellerId());

        // Act: Call the method
        productServiceImp.forceDeleteProduct(userId, sellerId, productId);

        System.out.println("Force delete product id: " + productId);
        System.out.println("Force delete product user id: " + userId);
        System.out.println("Force delete product seller id: " + sellerId);
    }
}

package com.codewithkirk.productService.UnitTesting;

import com.codewithkirk.productService.Dto.*;
import com.codewithkirk.productService.Exception.ProductOrderServiceUnavailableException;
import com.codewithkirk.productService.Model.Products;
import com.codewithkirk.productService.Repository.ProductRepository;
import com.codewithkirk.productService.Service.impl.ProductServiceImp;
import com.codewithkirk.productService.ServiceClient.OrderItems.OrderItemsServiceClient;
import com.codewithkirk.productService.ServiceClient.Orders.OrderServiceClient;
import com.codewithkirk.productService.ServiceClient.Sellers.SellerServiceClient;
import com.codewithkirk.productService.ServiceClient.Users.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.*;

import static com.codewithkirk.productService.Configuration.RabbitConfig.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
public class ProductServiceUnitTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserServiceClient userServiceClient; // Mocked UserServiceClient

    @Mock
    private SellerServiceClient sellerServiceClient; // Mocked UserServiceClient

    @Mock
    private OrderServiceClient orderServiceClient; // Mocked UserServiceClient

    @Mock
    private OrderItemsServiceClient orderItemsServiceClient; // Mocked UserServiceClient

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Logger logger;

    @InjectMocks
    private ProductServiceImp productServiceImp;

    private final Long userId = 1L;
    private final Long sellerId = 1L;
    private final Long productId = 1L;

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
        MockitoAnnotations.openMocks(this);

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

    @Test
    @Order(1)
    void shouldCreateNewProduct() {

        // Mock SellerServiceClient behavior
        when(sellerServiceClient.getSellerByUserIdAndSellerId(productDto.getUserId(),
                productDto.getSellerId()))
                .thenReturn(null); // Mock a return value (adjust as needed)

        // Stub the repository save method to return the saved seller
        when(productRepository.save(any(Products.class))).thenReturn(newProduct);

        // Act: Call the method under test
        Products result = productServiceImp.newProduct(productDto);

        // Assert: Verify the behavior and output
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(userId, result.getUserId());
        assertEquals(sellerId, result.getSellerId());
        assertEquals(productName, result.getProductName());
        assertEquals(skuCode, result.getSkuCode());


        // Verify save was called once
        verify(productRepository, times(1)).save(any(Products.class));
        // Verify UserServiceClient interaction
        verify(sellerServiceClient, times(1)).getSellerByUserIdAndSellerId(
                                                                    productDto.getUserId(),
                                                                    productDto.getSellerId());

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println("New Product: " + result);
    }

    @Test
    @Order(2)
    void shouldPurchaseProduct() {
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

        List<ProductItemsDto> orderItems = new ArrayList<>();
        // Adding multiple ProductItemsDto objects to the list
        orderItems.add(new ProductItemsDto(productId, 2, BigDecimal.valueOf(50.25)));
        orderItems.add(new ProductItemsDto(productId, 1, BigDecimal.valueOf(50.25)));
        productPurchaseDto.setOrderItems(orderItems);

        // Mocking service calls
        when(userServiceClient.getUserById(userId)).thenReturn(null);  // Mock a valid user
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Products()));  // Mock product found
        when(orderServiceClient.checkHealth()).thenReturn("{\"status\":\"DOWN\"}");  // Simulate Order Service down
        when(orderItemsServiceClient.checkHealth()).thenReturn("{\"status\":\"UP\"}");  // Order Items Service is up

        // Act & Assert
        ProductOrderServiceUnavailableException exception = assertThrows(
                ProductOrderServiceUnavailableException.class,
                () -> productServiceImp.productPurchase(productPurchaseDto)
        );

        // Assertions
        assertNotNull(exception);
        assertEquals("Order Service is down. Order processing aborted.", exception.getMessage());

        // Verify that no RabbitMQ messages are sent if the service is down
        verify(rabbitTemplate, times(0))
                .convertAndSend(eq(ORDER_EXCHANGE), eq(ORDER_ROUTING_KEY), any(OrderDto.class));
        verify(rabbitTemplate, times(0))
                .convertAndSend(eq(ORDER_ITEMS_EXCHANGE), eq(ORDER_ITEMS_ROUTING_KEY),
                        any(OrderItemsDto.class));

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product order id: " + orderId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product Purchase: " + productPurchaseDto);
    }

    @Test
    @Order(3)
    void shouldReturnAllProducts() {
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

        List<Products> products = Arrays.asList(newProduct, newProduct2);

        // Mocking the repository method to return the mock products list
        when(productRepository.findAll()).thenReturn(products);

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

        // Verify that the repository method was called once
        verify(productRepository, times(1)).findAll();

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
    @Order(4)
    void shouldReturnAllProductsBySellerId() {
        productName = "Iphone 14";
        skuCode = "2024120414TES213EWFQ";
        productDescription = "Iphone 14 Pro Max";
        productCategory = "Mobile Phones";
        price = BigDecimal.valueOf(1199.50);
        features = "Folded Phone and Fast Charging";
        stock = 500;
        availability = "In Stock";
        photoUrl = "http://example.com/iphon14.jpg";

        Long productId2 = 1L;
        Long sellerId2 = 1L;
        Long userId2 = 1L;

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

        List<Products> products = Arrays.asList(newProduct, newProduct2);

        // Mocking the repository method to return the mock products list
        when(productRepository.findAllProductsBySellerId(sellerId)).thenReturn(products);

        // Act: Call the method
        List<Products> result = productServiceImp.showAllProductsBySellerId(sellerId);

        // Assert: Verify the result
        assertNotNull(result);

        assertEquals(2, result.size());
        assertEquals(userId, newProduct.getUserId());
        assertEquals(sellerId, newProduct.getSellerId());
        assertEquals(productId, newProduct.getProductId());

        // Verify that the repository method was called once
        verify(productRepository, times(1))
                .findAllProductsBySellerId(sellerId);

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println("Products: " + result);
    }

    @Test
    @Order(5)
    void shouldReturnProductById() {
        // Mocking the repository to return the product when searched by sellerId and productId
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(newProduct));

        // Act: Call the method
        Optional<Products> result = productServiceImp.showProductById(productId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(productId, result.get().getProductId());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Verify that the repository method was called once with the correct arguments
        verify(productRepository, times(1))
                .findById(productId);

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println("Product: " + result);
    }

    @Test
    @Order(6)
    void shouldReturnProductBySellerIdAndProductId() {

        // Mocking the repository to return the product when searched by sellerId and productId
        when(productRepository.findProductBySellerIdAndProductId(sellerId, productId))
                .thenReturn(Optional.of(newProduct));

        // Act: Call the method
        Optional<Products> result = productServiceImp.showProductBySellerIdAndProductId(sellerId, productId);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(productId, result.get().getProductId());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Verify that the repository method was called once with the correct arguments
        verify(productRepository, times(1))
                .findProductBySellerIdAndProductId(sellerId, productId);

        // Print the result to the console
        System.out.println("Product id: " + productId);
        System.out.println("Product user id: " + userId);
        System.out.println("Product seller id: " + sellerId);
        System.out.println("Product: " + result);
    }

    @Test
    @Order(7)
    void shouldUpdateProduct() {
        // Mock SellerServiceClient behavior
        when(sellerServiceClient.getSellerByUserIdAndSellerId(productDto.getUserId(),
                productDto.getSellerId()))
                .thenReturn(null); // Mock a return value (adjust as needed)

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

        when(productRepository.findProductBySellerIdAndProductId(sellerId, productId))
                .thenReturn(Optional.of(updateProduct));
        when(productRepository.save(any(Products.class))).thenReturn(updateProduct);


        // Call method
        Products result = productServiceImp.updateProduct(userId, sellerId,
                productId, updateProductDto);

        // Verify
        assertNotNull(result);
        assertEquals(productId, updateProductDto.getProductId());
        assertEquals(sellerId, updateProductDto.getSellerId());
        assertEquals(userId, updateProductDto.getUserId());

        verify(productRepository, times(1)).save(any(Products.class));

        System.out.println("Updated product id: " + productId);
        System.out.println("Updated product seller id: " + sellerId);
        System.out.println("Updated product user id: " + userId);
        System.out.println("Updated product: " + result);
    }

    @Test
    @Order(8)
    void shouldForceDeleteProduct() {
        // Mock SellerServiceClient behavior
        when(sellerServiceClient.getSellerByUserIdAndSellerId(productDto.getUserId(),
                productDto.getSellerId()))
                .thenReturn(null); // Mock a return value (adjust as needed)

        when(productRepository.findProductBySellerIdAndProductId(sellerId, productId))
                .thenReturn(Optional.of(new Products()));

        // Act: Call the method
        productServiceImp.forceDeleteProduct(userId, sellerId, productId);

        // Verify interactions with mocks
        verify(sellerServiceClient, times(1))
                .getSellerByUserIdAndSellerId(userId, sellerId);
        verify(productRepository, times(1))
                .findProductBySellerIdAndProductId(sellerId, productId);

        System.out.println("Force delete product id: " + productId);
        System.out.println("Force delete product user id: " + userId);
        System.out.println("Force delete product seller id: " + sellerId);
    }
}

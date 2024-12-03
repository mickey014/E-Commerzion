package com.codewithkirk.sellerService.IntegrationTesting;

import com.codewithkirk.sellerService.Dto.SellerDto;
import com.codewithkirk.sellerService.IntegrationTesting.Config.SellerServiceIntegrationTestConfig;
import com.codewithkirk.sellerService.Model.Sellers;
import com.codewithkirk.sellerService.Repository.SellerRepository;
import com.codewithkirk.sellerService.Service.impl.SellerServiceImp;
import com.codewithkirk.sellerService.ServiceClient.UserServiceClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SellerServiceIntegrationTest extends SellerServiceIntegrationTestConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SellerServiceImp sellerServiceImp;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private SellerRepository sellerRepository;

    private SellerDto sellerDto;
    private Sellers newSeller;

    private String sellerName;
    private String storeName;
    private String email;
    private String phoneNumber;
    private String location;
    private Sellers.SellerStatus status;
    private String photoUrl;

    private final Long userId = 1L;
    private final Long sellerId = 1L;

    @BeforeEach
    void setUp() {
        // Arrange reusable test data
        sellerName = "John Doe";
        storeName = "J Doe Tech shop";
        email = "johndoe@gmail.com";
        phoneNumber = "09309800034";
        location = "John Street Meycawan";
        status = Sellers.SellerStatus.ACTIVE;
        photoUrl = "https://test.com";

        sellerDto = new SellerDto(
                sellerId,
                userId,
                sellerName,
                storeName,
                email,
                phoneNumber,
                location,
                status,
                photoUrl
        );

        newSeller = Sellers.builder()
                .sellerId(sellerId)
                .userId(userId)
                .sellerName(sellerName)
                .storeName(storeName)
                .email(email)
                .phoneNumber(phoneNumber)
                .location(location)
                .status(status)
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
    void shouldRegisterSeller() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        userServiceClient.getUserById(sellerDto.getUserId());
        // Act Call the method under test
        Sellers result = sellerServiceImp.registerSeller(sellerDto);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(sellerId, result.getSellerId());
        assertEquals(email, result.getEmail());
        assertEquals(phoneNumber, result.getPhoneNumber());


        if(result != null) {
            System.out.println("Registered seller id: " + sellerId);
            System.out.println("Registered user id: " + userId);
            System.out.println("Registered seller: " + result);
        }
    }

    @Test
    @Order(2)
    void shouldReturnSellerByUserIdAndSellerId() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");
        userServiceClient.getUserById(sellerDto.getUserId());

        // Act
        Optional<Sellers> result = sellerServiceImp
                .getSellerByUserIdAndSellerId(userId, sellerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Print the result to the console
        System.out.println("User id: " + userId);
        System.out.println("Seller id: " + sellerId);
        System.out.println("Seller: " + result);
    }

    @Test
    @Order(3)
    void shouldReturnSellerById() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");
        userServiceClient.getUserById(sellerDto.getUserId());

        // Act
        Optional<Sellers> result = sellerServiceImp
                .getSellerById(sellerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Print the result to the console
        System.out.println("User id: " + userId);
        System.out.println("Seller id: " + sellerId);
        System.out.println("Seller: " + result);
    }

    @Test
    @Order(4)
    void shouldUpdateSeller() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        // Arrange reusable test data
        sellerName = "John Doe test";
        storeName = "J Doe Tech shop tset";
        email = "johndoetest@gmail.com";
        phoneNumber = "09309800035";
        location = "John Street Meycawan test";
        status = Sellers.SellerStatus.ACTIVE;
        photoUrl = "https://testtest.com";

        SellerDto updateSellerDto = new SellerDto(
                sellerId,
                userId,
                sellerName,
                storeName,
                email,
                phoneNumber,
                location,
                status,
                photoUrl
        );

        // Build the new seller object using the Builder pattern
        Sellers updateSeller = Sellers.builder()
                .sellerId(sellerId)
                .userId(userId)
                .sellerName(sellerName)
                .storeName(storeName)
                .email(email)
                .phoneNumber(phoneNumber)
                .location(location)
                .photoUrl(photoUrl)
                .status(status)
                .build();

        userServiceClient.getUserById(updateSellerDto.getUserId());

        // Call method
        Sellers result = sellerServiceImp.updateSeller(userId, sellerId, updateSellerDto);

        // Verify
        assertNotNull(result);
        assertEquals(email, updateSellerDto.getEmail());
        assertEquals(phoneNumber, updateSellerDto.getPhoneNumber());

        System.out.println("Updated user id: " + userId);
        System.out.println("Updated seller id: " + sellerId);
        System.out.println("Updated seller: " + result);
    }

    @Test
    @Order(5)
    void shouldSafeDeleteSeller() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        Sellers existingSeller = Sellers.builder()
                .sellerId(sellerId)
                .userId(userId)
                .status(Sellers.SellerStatus.ACTIVE)
                .build();

        userServiceClient.getUserById(existingSeller.getUserId());

        // Act: Call the method
        Sellers result = sellerServiceImp.safeDeleteSeller(userId, sellerId);

        // Assert: Validate the outcome
        assertNotNull(result);
        assertEquals(Sellers.SellerStatus.INACTIVE, result.getStatus());
        assertEquals(sellerId, result.getSellerId());
        assertEquals(userId, result.getUserId());

        if(result != null) {
            System.out.println("Safe delete seller id: " + sellerId);
            System.out.println("Safe delete user id: " + userId);
            System.out.println("Safe delete seller: " + result.getStatus());
        }
    }

    @Test
    @Order(6)
    void shouldForceDeleteSeller() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");
        userServiceClient.getUserById(userId);

        // Act: Call the method
        sellerServiceImp.forceDeleteSeller(userId, sellerId);

        System.out.println("Force delete seller id: " + sellerId);
        System.out.println("Force delete user id: " + userId);
    }

}

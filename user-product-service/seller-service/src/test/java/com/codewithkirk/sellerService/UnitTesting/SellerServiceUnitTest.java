package com.codewithkirk.sellerService.UnitTesting;

import com.codewithkirk.sellerService.Dto.SellerDto;
import com.codewithkirk.sellerService.Model.Sellers;
import com.codewithkirk.sellerService.Repository.SellerRepository;
import com.codewithkirk.sellerService.Service.impl.SellerServiceImp;
import com.codewithkirk.sellerService.ServiceClient.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
public class SellerServiceUnitTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserServiceClient userServiceClient; // Mocked UserServiceClient

    @InjectMocks
    private SellerServiceImp sellerServiceImp;

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
        MockitoAnnotations.openMocks(this);

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

    @Test
    @Order(1)
    void shouldRegisterSeller() {

        // Mock UserServiceClient behavior
        when(userServiceClient.getUserById(sellerDto.getUserId())).thenReturn(null); // Mock a return value (adjust as needed)

        // Build the new seller object using the Builder pattern
        Sellers newSeller = Sellers.builder()
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

        // Stub the repository save method to return the saved seller
        when(sellerRepository.save(any(Sellers.class))).thenReturn(newSeller);

        // Act: Call the method under test
        Sellers result = sellerServiceImp.registerSeller(sellerDto);

        // Assert: Verify the behavior and output
        assertNotNull(result, "Result should not be null");
        assertEquals(email, result.getEmail(), "Email should match");
        assertEquals(phoneNumber, result.getPhoneNumber(), "Phone number should match");

        // Verify save was called once
        verify(sellerRepository, times(1)).save(any(Sellers.class));
        // Verify UserServiceClient interaction
        verify(userServiceClient, times(1)).getUserById(sellerDto.getUserId());

        // Print the result to the console
        System.out.println("Registered seller id: " + sellerId);
        System.out.println("Registered seller: " + result);
    }

    @Test
    @Order(2)
    void shouldReturnSellerByUserIdAndSellerId() {

        when(sellerRepository.findSellerByUserIdAndSellerId(userId, sellerId))
                .thenReturn(Optional.of(newSeller));

        // Act
        Optional<Sellers> result = sellerServiceImp
                .getSellerByUserIdAndSellerId(userId, sellerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Verify interactions
        verify(sellerRepository, times(1))
                .findSellerByUserIdAndSellerId(userId, sellerId);

        // Print the result to the console
        System.out.println("User id: " + userId);
        System.out.println("Seller id: " + sellerId);
        System.out.println("Seller: " + result);
    }

    @Test
    @Order(3)
    void shouldReturnSellerById() {

        when(sellerRepository.findById(sellerId))
                .thenReturn(Optional.of(newSeller));

        // Act
        Optional<Sellers> result = sellerServiceImp
                .getSellerById(sellerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals(sellerId, result.get().getSellerId());

        // Verify interactions
        verify(sellerRepository, times(1))
                .findById(sellerId);

        // Print the result to the console
        System.out.println("Seller id: " + sellerId);
        System.out.println("Seller: " + result);
    }


}

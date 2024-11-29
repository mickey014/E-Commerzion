package com.codewithkirk.userService.UnitTesting;

import com.codewithkirk.userService.Dto.UserDtoResponse;
import com.codewithkirk.userService.Model.Users;
import com.codewithkirk.userService.Repository.UserRepository;
import com.codewithkirk.userService.Service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void shouldRegisterUser() {
        // Arrange: Prepare test data and mock behavior
        UserDtoResponse userDto = new UserDtoResponse(
                userId,
                "John Doe",
                "johndoe123",
                "",
                "test",
                "test",
                "",
                Users.UserStatus.ACTIVE
        );

        Users savedUser = Users.builder()
                .userId(userId)
                .name("John Doe")
                .username("johndoe123")
                .password("test")
                .photoUrl("")
                .status(Users.UserStatus.ACTIVE)
                .build();

        // Mock and Stub the repository's save method to return the saved user
        when(userRepository.save(any(Users.class))).thenReturn(savedUser);

        // Act: Call the method under test
        Users result = userServiceImpl.registerUser(userDto);

        // Assert: Verify the behavior and output
        assertNotNull(result);
        assertEquals("johndoe123", result.getUsername());
        assertEquals("test", result.getPassword());

        // Verify save was called once
        verify(userRepository, times(1)).save(any(Users.class));

        // Print the result to the console
        if(result != null) {
            System.out.println("Registered user id: " + userId);
            System.out.println("Registered User: " + result);
        }
    }

    @Test
    @Order(2)
    void shouldLoginUser(){
        // Arrange: Set up mock user data and repository behavior
        String username = "johndoe123";
        String password = "test";

        Users savedUser = Users.builder()
                .userId(userId)
                .name("John Doe")
                .username("johndoe123")
                .password("test")
                .photoUrl("")
                .status(Users.UserStatus.ACTIVE)
                .build();

        // Mock and Stub the repository's save method to return the saved user
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        // Act: Call the method under test
        Users result = userServiceImpl.loginUser(username, password);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());

        // Verify repository interaction
        verify(userRepository, times(1)).findByUsername(username);

        // check if user is exists
        if (result != null) {
            System.out.println("Logged In Successfully: " + result.getName());
        }

    }

    @Test
    @Order(3)
    void shouldUpdateUser() {
        // Arrange: Prepare test data and mock behavior
        UserDtoResponse userDto = new UserDtoResponse(
                userId,
                "John Doe",
                "johndoe123",
                "johndoe@gmail.com",
                "test",
                "test",
                "https://google.com.ph/storage/profile/",
                Users.UserStatus.ACTIVE
        );

        Users savedUser = Users.builder()
                .userId(userId)
                .name("John Doe")
                .username("johndoe123")
                .email("johndoe@gmail.com")
                .password("test")
                .photoUrl("https://google.com.ph/storage/profile/")
                .status(Users.UserStatus.ACTIVE)
                .build();

        // Mock and Stub the repository's findById method to return the saved user
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        // Mock and Stub the repository's save method to return the updated user
        when(userRepository.save(any(Users.class))).thenReturn(savedUser);

        // Act: Call the method under test
        Users result = userServiceImpl.updateUser(userId, userDto);

        // Assert: Verify the behavior and output
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("johndoe123", result.getUsername());
        assertEquals("johndoe@gmail.com", result.getEmail());
        assertEquals("test", result.getPassword());

        // Verify save was called once
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(Users.class));

        // Print the result to the console
        if(result != null) {
            System.out.println("Updated user id: " + userId);
            System.out.println("Updated user: " + result);
        }
    }

    @Test
    @Order(4)
    void shouldSafeDeleteUser() {

        // Arrange: Prepare test data and mock behavior
        Users existingUser = Users.builder()
                .userId(userId)
                .name("John Doe")
                .username("johndoe123")
                .email("johndoe@gmail.com")
                .password("test")
                .photoUrl("https://google.com.ph/storage/profile/")
                .status(Users.UserStatus.ACTIVE)
                .build();

        // Mock the repository's findById method to return
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // stub the repository's save method to return the updated user with INACTIVE status mockito
        when(userRepository.save(any(Users.class))).thenAnswer( invocation -> {
            Users updatedUser = invocation.getArgument(0);
            updatedUser.setStatus(Users.UserStatus.INACTIVE);
            return updatedUser;
        });

        // Act: Call the method under test
        Users result = userServiceImpl.safeDeleteUser(userId);

        // Assert: Verify the behaviour and output
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(Users.UserStatus.INACTIVE, result.getStatus());

        // Verify that findById was called once and save was called once
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(Users.class));

        // Print the result to the console
        if(result != null) {
            System.out.println("Safe delete user id: " + userId);
            System.out.println("Safe delete user: " + result);
        }
    }

    @Test
    @Order(5)
    void shouldForceDeleteUser() {

        // Arrange: Prepare test data and mock behavior
        Users existingUser = Users.builder()
                .userId(userId)
                .name("John Doe")
                .username("johndoe123")
                .email("johndoe@gmail.com")
                .password("test")
                .photoUrl("https://google.com.ph/storage/profile/")
                .status(Users.UserStatus.ACTIVE)
                .build();

        // Mock the repository's findById method to return existing users
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act: Call the method under the test
        userServiceImpl.forceDeleteUser(userId);

        // Verify that the delete method was called once with the correct user
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(existingUser);

        // Print the result to the console
        System.out.println("Force delete user id: " + userId);
    }

}

package com.codewithkirk.userService.IntegrationTesting;

import com.codewithkirk.userService.Dto.UserDtoResponse;
import com.codewithkirk.userService.IntegrationTesting.Config.UserServiceIntegrationTestConfig;
import com.codewithkirk.userService.Model.Users;
import com.codewithkirk.userService.Repository.UserRepository;
import com.codewithkirk.userService.Service.impl.UserServiceImpl;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class UserServiceIntegrationTest extends UserServiceIntegrationTestConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserRepository userRepository;

    private final Long userId = 1L;

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
    void shouldRegisterUser() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

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

        // Act Call the method under test
        Users result = userServiceImpl.registerUser(userDto);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals("johndoe123", result.getUsername());
        assertEquals("test", result.getPassword());

        if(result != null) {
            System.out.println("Registered user id: " + userId);
            System.out.println("Registered User: " + result);
        }
    }

    @Test
    @Order(2)
    void shouldLoginUser(){
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        String username = "johndoe123";
        String password = "test";

        // Act: Call the method under test
        Users result = userServiceImpl.loginUser(username, password);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());

        if (result != null) {
            System.out.println("Logged In Successfully: " + result.getName());
        }
    }

    @Test
    @Order(3)
    void shouldUpdateUser() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

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

        // Act: Call the method under test
        Users result = userServiceImpl.updateUser(userId, userDto);

        // Assert: Verify the behavior and output
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("johndoe123", result.getUsername());
        assertEquals("johndoe@gmail.com", result.getEmail());
        assertEquals("test", result.getPassword());


        // Print the result to the console
        if(result != null) {
            System.out.println("Updated user id: " + userId);
            System.out.println("Updated user: " + result);
        }
    }

    @Test
    @Order(4)
    void shouldSafeDeleteUser() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        // Act: Call the method under test
        Users result = userServiceImpl.safeDeleteUser(userId);

        // Assert: Verify the behaviour and output
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(Users.UserStatus.INACTIVE, result.getStatus());


        // Print the result to the console
        if(result != null) {
            System.out.println("Safe delete user id: " + userId);
            System.out.println("Safe delete user: " + result);
        }
    }

    @Test
    @Order(5)
    void shouldForceDeleteUser() {
        System.out.println("Connected to " + postgreSqlContainer.getDatabaseName() + " Integration test");

        // Act: Call the method under the test
        userServiceImpl.forceDeleteUser(userId);

        System.out.println("Force delete user id: " + userId);
    }
}

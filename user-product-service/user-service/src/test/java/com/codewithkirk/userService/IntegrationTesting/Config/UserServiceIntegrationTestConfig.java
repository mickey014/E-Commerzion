package com.codewithkirk.userService.IntegrationTesting.Config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class UserServiceIntegrationTestConfig {

    // Define the PostgreSQL container
    protected static final PostgreSQLContainer<?> postgreSqlContainer =
            new PostgreSQLContainer<>("postgres:14-alpine")
                    .withDatabaseName("users")
                    .withUsername("postgres")
                    .withPassword("test")
					.withExposedPorts(5100)  // Optionally expose port 5432
					.withNetworkMode("bridge");

    // Start the container statically
    static {
        postgreSqlContainer.start();
    }

    // Dynamically register properties for Spring
    @DynamicPropertySource
    protected static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        registry.add("spring.datasource.password", postgreSqlContainer::getPassword);
    }
}

package com.codewithkirk.orderService.IntegrationTesting.Config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class OrderServiceIntegrationTestConfig {

    // Define the PostgreSQL container
    protected static final PostgreSQLContainer<?> postgreSqlContainer =
            new PostgreSQLContainer<>("postgres:14-alpine")
                    .withDatabaseName("orders")
                    .withUsername("postgres")
                    .withPassword("test");

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

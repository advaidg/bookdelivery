package com.example.demo.base;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTestContainerConfiguration {

    private AbstractTestContainerConfiguration() {
    }

    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.33");

    @BeforeAll
    static void beforeAll() {
        mysqlContainer.withReuse(true);
        mysqlContainer.start();
    }

    @DynamicPropertySource
    private static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.username", mysqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mysqlContainer::getPassword);
        dynamicPropertyRegistry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
    }

}

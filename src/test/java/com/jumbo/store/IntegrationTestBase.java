package com.jumbo.store;

import com.jumbo.store.config.TestConfig;
import com.jumbo.store.fixtures.RepoHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@Import({TestConfig.class, RepoHelper.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = StoreServiceApplication.class)
@TestPropertySource(properties = {"store.data.loader.enabled=false", "spring.cache.type=none"})
public abstract class IntegrationTestBase {

    protected static final String POSTGRES_IMAGE_NAME = "postgres:16-alpine";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME)
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @LocalServerPort
    protected int port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.baseline-on-migrate", () -> true);
        registry.add("spring.flyway.clean-disabled", () -> true);
        registry.add("jwt.secret", () -> "testSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong");
    }

    @BeforeEach
    public void init() {
        RestAssured.port = port;
    }
}

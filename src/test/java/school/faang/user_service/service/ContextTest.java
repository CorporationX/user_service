package school.faang.user_service.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class ContextTest {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.3");

    @BeforeAll
    static void runContainer() {
        container.start();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }

    @Test
    void contextLoads() {
    }
}

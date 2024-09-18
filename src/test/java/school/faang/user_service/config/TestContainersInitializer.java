package school.faang.user_service.config;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainersInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext>, AfterAllCallback {

    private static final PostgreSQLContainer POSTGRES_SQL_CONTAINER = new PostgreSQLContainer(
            "postgres:14.1")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        POSTGRES_SQL_CONTAINER.start();

        TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRES_SQL_CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRES_SQL_CONTAINER.getUsername(),
                "spring.datasource.password=" + POSTGRES_SQL_CONTAINER.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        POSTGRES_SQL_CONTAINER.close();
    }
}
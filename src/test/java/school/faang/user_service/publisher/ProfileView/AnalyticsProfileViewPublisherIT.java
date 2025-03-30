package school.faang.user_service.publisher.ProfileView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.config.Kafka.EventListener;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.user.UserController;
import school.faang.user_service.event.AnalyticsProfileViewEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/UserService/drop.sql", "/UserService/user_initial.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/UserService/drop.sql", executionPhase = AFTER_TEST_METHOD)
@Import(EventListener.class)
@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
public class AnalyticsProfileViewPublisherIT {

    @Autowired
    private UserController userController;

    @Autowired
    private UserContext userContext;

    @Autowired
    private EventListener eventListener;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.5"));

    @Container
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(
            DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.topics.test-topic.name", () -> "analytics_user_view_profile_topic");

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @AfterEach
    void afterTest() {
        eventListener.setReceivedMessage(null);
    }

    @Test
    void testAnalyticsEventPublisher_UserExist() throws IOException, InterruptedException {
        userContext.setUserId(1L);
        AnalyticsProfileViewEvent expectedEvent = AnalyticsProfileViewEvent.builder()
                .userId(2L)
                .viewerUserId(1L)
                .build();

        userController.getUser(2L);

        await()
                .pollInterval( 2, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertNotNull(eventListener.getReceivedMessage());

                    AnalyticsProfileViewEvent actualEvent = objectMapper.readValue(
                            eventListener.getReceivedMessage(), AnalyticsProfileViewEvent.class);

                    assertEquals(expectedEvent.getUserId(), actualEvent.getUserId());
                    assertEquals(expectedEvent.getViewerUserId(), actualEvent.getViewerUserId());
                });
        System.out.println();
    }

    @Test
    void testAnalyticsEventPublisher_UserNonExist() throws IOException {
        Long nonExistentUserId = 3L;
        userContext.setUserId(3L);

        userController.getUser(2L);

        assertNull(eventListener.getReceivedMessage());
    }
}

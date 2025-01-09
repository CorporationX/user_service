package school.faang.user_service.controller.recommendation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.UserServiceApplication;
import school.faang.user_service.dto.event.RecommendationEvent;
import school.faang.user_service.publisher.RecommendationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = UserServiceApplication.class)
@Testcontainers
class RecommendationEventPublisherIntegrationTest {

    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0.0").withExposedPorts(6379);


    @DynamicPropertySource
    static void redisProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }


    @Autowired
    private RecommendationEventPublisher recommendationEventPublisher;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testPublishToRecommendationPublishesCorrectMessage() throws Exception {

        RecommendationEvent event = new RecommendationEvent(1L, 2L, 3L, LocalDateTime.now());


        redisTemplate.getConnectionFactory().getConnection().flushAll();


        CountDownLatch latch = new CountDownLatch(1);
        List<RecommendationEvent> receivedMessages = new ArrayList<>();


        redisConnectionFactory.getConnection().subscribe(
                (message, pattern) -> {
                    try {
                        RecommendationEvent receivedEvent = objectMapper.readValue(message.getBody(), RecommendationEvent.class);
                        receivedMessages.add(receivedEvent);
                        latch.countDown(); // Signal that the message was received
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to deserialize message", e);
                    }
                },
                "recommendation_event_topic".getBytes()
        );


        recommendationEventPublisher.publishToRecommendation(event);


        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertEquals(true, messageReceived, "No message was received within the timeout period.");
        assertEquals(1, receivedMessages.size(), "Unexpected number of messages received.");
        assertEquals(event, receivedMessages.get(0), "The published event does not match the received event.");
    }
}
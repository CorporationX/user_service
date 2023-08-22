package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.model.EventType;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.RecommendationReceivedEvent;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(value = {MockitoExtension.class})
public class RecommendationReceivedEventPublisherTest {

    @InjectMocks
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @Spy
    private ObjectMapper objectMapper;
    private RecommendationReceivedEvent test;
    private String json;

    @BeforeEach
    void init() throws JsonProcessingException {
        test = new RecommendationReceivedEvent();
        test.setRecommendationId(3L);
        test.setAuthorId(1L);
        test.setRecipientId(2L);
        test.setEventType(EventType.RECOMMENDATION_RECEIVED);
        test.setReceivedAt(new Date());
        json = objectMapper.writeValueAsString(test);
    }

    @Test
    public void testSendEvent() throws JsonProcessingException {

        recommendationReceivedEventPublisher.sendEvent(1L, 2L, 3L);
        Mockito.verify(objectMapper).writeValueAsString(any());
        Mockito.verify(redisMessagePublisher).publish(any(), any());
    }
}

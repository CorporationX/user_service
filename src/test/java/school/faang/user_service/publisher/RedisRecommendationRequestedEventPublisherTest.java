package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.RecommendationRequestedEvent;
import school.faang.user_service.service.recommendation.RedisRecommendationRequestedEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.mockito.Mockito.verify;

class RedisRecommendationRequestedEventPublisherTest {

    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic topic;
    private RedisRecommendationRequestedEventPublisher publisher;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        topic = new ChannelTopic("recommendation-requested");
        publisher = new RedisRecommendationRequestedEventPublisher(redisTemplate, topic);
    }

    @Test
    void publish_shouldSendEventToCorrectTopic() {
        RecommendationRequestedEvent event = new RecommendationRequestedEvent(1L, 2L, 3L);

        publisher.publish(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        verify(redisTemplate, times(1)).convertAndSend(topicCaptor.capture(), eventCaptor.capture());

        assertEquals("recommendation-requested", topicCaptor.getValue());
        assertEquals(event, eventCaptor.getValue());
    }
}
package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.RecommendationRequestEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestEventPublisherTest {

    private static final Long RECEIVER_ID = 1L;
    private static final Long REQUESTER_ID = 2L;
    private static final Long RECOMMENDATION_ID = 3L;
    private static final String CHANNEL_NAME = "recommendation_request_channel";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @InjectMocks
    private RecommendationRequestEventPublisher recommendationRequestEventPublisher;

    private RecommendationRequestEvent event;

    @BeforeEach
    void setUp() {
        RedisProperties.Channel channel = new RedisProperties.Channel();
        channel.setRecommendationRequestChannel(CHANNEL_NAME);

        when(redisProperties.getChannel()).thenReturn(channel);

        event = RecommendationRequestEvent.builder()
                .requestId(RECOMMENDATION_ID)
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .build();
    }

    @Test
    @DisplayName("RecommendationRequestEvent published successfully")
    void testPublish_success() {
        when(redisTemplate.convertAndSend(CHANNEL_NAME, event)).thenReturn(1L);

        recommendationRequestEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(CHANNEL_NAME, event);
    }

    @Test
    @DisplayName("RecommendationRequestEvent throws exception")
    void testPublish_withException() {
        when(redisTemplate.convertAndSend(CHANNEL_NAME, event))
                .thenThrow(new RuntimeException("Redis server unreachable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recommendationRequestEventPublisher.publish(event);
        });

        assertEquals("Redis server unreachable", exception.getMessage());

        verify(redisTemplate, times(1)).convertAndSend(CHANNEL_NAME, event);
    }
}

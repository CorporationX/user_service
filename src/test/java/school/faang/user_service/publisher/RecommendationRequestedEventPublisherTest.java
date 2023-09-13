package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.redis.EventRecommendationRequestDto;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.recommendation_requested_event_channel}")
    private String topicRecommendationRequestedEvent;

    private RecommendationRequestedEventPublisher publisher;

    @Test
    public void testPublish() {
        EventRecommendationRequestDto event = EventRecommendationRequestDto.builder().build();
        redisTemplate.convertAndSend(topicRecommendationRequestedEvent, event);

        verify(redisTemplate).convertAndSend(topicRecommendationRequestedEvent, event);

    }
}
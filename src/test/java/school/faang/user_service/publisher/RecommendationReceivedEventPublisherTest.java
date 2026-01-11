package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceivedEventPublisherTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    RecommendationReceivedEventPublisher publisher;

    @Test
    public void publish_sendsToCorrectChannel() {
        ReflectionTestUtils.setField(publisher, "recommendationTopicName", "recommendation_test");
        RecommendationReceivedEvent event = new RecommendationReceivedEvent(1L, 1L, 2L);
        publisher.publish(event);
        verify(redisTemplate).convertAndSend("recommendation_test", event);
    }
}

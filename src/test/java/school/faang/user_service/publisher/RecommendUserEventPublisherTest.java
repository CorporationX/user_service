package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.RecommendationEventDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendUserEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic recommendationTopic;

    @InjectMocks
    private RecommendUserPublisher recommendUserPublisher;

    @Test
    public void test_publishIsOk() {
        RecommendationEventDto evt = RecommendationEventDto
                .builder()
                .authorId(1L)
                .receiverId(2L)
                .recommendedAt(LocalDateTime.now())
                .build();
        recommendUserPublisher.publish(evt);
        verify(redisTemplate).convertAndSend(recommendationTopic.getTopic(), evt);
    }
}

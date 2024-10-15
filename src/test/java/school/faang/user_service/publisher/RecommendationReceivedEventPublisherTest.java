package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.event.RecommendationReceivedEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationReceivedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic recommendationReceivedTopic;

    @InjectMocks
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Test
    void publish_isOk() {
        // given
        var recommendationReceivedEvent = RecommendationReceivedEvent.builder().build();
        // when
        recommendationReceivedEventPublisher.publish(recommendationReceivedEvent);
        // then
        verify(redisTemplate).convertAndSend(recommendationReceivedTopic.getTopic(), recommendationReceivedEvent);
    }
}

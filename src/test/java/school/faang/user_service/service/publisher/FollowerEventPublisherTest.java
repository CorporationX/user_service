package school.faang.user_service.service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.FollowerEvent;
import school.faang.user_service.publisher.FollowerEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private FollowerEventPublisher publisher;

    @Test
    void shouldPublishEventToRedisChannel() {
        FollowerEvent event = new FollowerEvent(1L, 2L, null, LocalDateTime.now());
        when(topic.getTopic()).thenReturn("follower-events");

        publisher.publish(event);

        verify(redisTemplate).convertAndSend("follower-events", event);
    }
}

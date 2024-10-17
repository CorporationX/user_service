package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.FollowerEvent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Spy
    private RedisProperties redisProperties;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @Captor
    private ArgumentCaptor<FollowerEvent> followerEventCaptor;

    private final String channelName = "follower-event-channel";

    @BeforeEach
    void setUp() {
        Map<String, String> channels = Map.of("follower-event-channel", channelName);
        redisProperties.setChannels(channels);
    }

    @Test
    void publish_ShouldSendEventToRedisChannel() {
        FollowerEvent event = new FollowerEvent();

        followerEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(eq(channelName), followerEventCaptor.capture());
        FollowerEvent capturedEvent = followerEventCaptor.getValue();
        assertEquals(event, capturedEvent);
    }
}

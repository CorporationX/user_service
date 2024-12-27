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
import school.faang.user_service.event.EventStartEvent;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStartEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private RedisProperties redisProperties;

    @InjectMocks
    private EventStartEventPublisher eventStartEventPublisher;

    @BeforeEach
    void setUp() {
        redisProperties = TestRedisPropertiesFactory.createDefaultRedisProperties();

        eventStartEventPublisher =
                new EventStartEventPublisher(redisTemplate, null, redisProperties);
    }

    @Test
    @DisplayName("Publish message in redis success")
    void testPublish_success() {
        EventStartEvent event = EventStartEvent.builder()
                .eventId(1L)
                .attendeesIds(List.of(new Long[]{1L, 2L, 3L}))
                .build();
        String channelName = redisProperties.channel().eventStartEventChannel();

        eventStartEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(channelName, event);
    }
}

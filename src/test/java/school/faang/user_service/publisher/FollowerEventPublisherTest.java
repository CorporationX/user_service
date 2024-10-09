package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.follower.FollowerEventDto;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    private final String followerEvent = "test-follower-event-channel";
    private FollowerEventDto event;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        Field field = FollowerEventPublisher.class.getDeclaredField("followerEvent");
        field.setAccessible(true);
        field.set(followerEventPublisher, followerEvent);
    }

    @Test
    void publish_shouldConvertAndSendEvent() {
        // given
        event = FollowerEventDto.builder().followerId(1L).followeeId(2L).build();
        // when
        followerEventPublisher.publish(event);
        // then
        verify(redisTemplate).convertAndSend(eq(followerEvent), eq(event));
    }

    @Test
    void convertAndSend_shouldThrowRuntimeException_whenSerializationFails() {
        // given
        event = FollowerEventDto.builder().followerId(1L).followeeId(2L).build();
        doThrow(new RuntimeException("Serialization error")).when(redisTemplate)
                .convertAndSend(eq(followerEvent), eq(event));
        // when & then
        assertThrows(RuntimeException.class, () -> followerEventPublisher.publish(event));
        verify(redisTemplate).convertAndSend(eq(followerEvent), eq(event));
    }
}
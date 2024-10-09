package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

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
    void publish_shouldConvertAndSendEvent() throws JsonProcessingException {
        // given
        event = FollowerEventDto.builder().build();
        String json = "{\"userId\": 1, \"followerId\": 2}";
        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        // when
        followerEventPublisher.publish(event);
        // then
        verify(redisTemplate).convertAndSend(eq(followerEvent), eq(json));
    }

    @Test
    void convertAndSend_shouldThrowRuntimeException_whenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        // given
        event = FollowerEventDto.builder().followerId(1L).followeeId(2L).build();
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Test exception") {});
        // when & then
        assertThrows(RuntimeException.class, () -> followerEventPublisher.publish(event));
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}
package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.FollowerEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    private final String followerTopic = "follower_channel";

    private FollowerEvent event;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(followerEventPublisher, "followerTopic", followerTopic);

        event = FollowerEvent.builder()
                .followerId(1L)
                .followeeId(2L)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSendEventSuccessfully() throws JsonProcessingException {
        String expectedJson = objectMapper.writeValueAsString(event);

        followerEventPublisher.publish(event);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(redisTemplate, times(1)).convertAndSend(eq(followerTopic), argumentCaptor.capture());
        assertEquals(expectedJson, argumentCaptor.getValue());
    }

    @Test
    void shouldThrowExceptionWhenSerializationFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {
        });

        assertThrows(RuntimeException.class, () -> followerEventPublisher.publish(event));
    }
}
package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.exception.EventPublishingException;
import school.faang.user_service.service.event.FollowerEvent;
import school.faang.user_service.service.event.FollowerEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(channelTopic.getTopic()).thenReturn("followerEventTopic");
    }

    @Test
    public void testPublishSuccess() throws JsonProcessingException {
        FollowerEvent event = new FollowerEvent(1L, 2L, 3L, LocalDateTime.now());

        when(objectMapper.writeValueAsString(event)).thenReturn("{\"followerId\": 1, \"followedUserId\": 2, " +
                "\"followedProjectId\": 3}");

        followerEventPublisher.publish(event);

        verify(objectMapper).writeValueAsString(event);

        verify(redisTemplate).convertAndSend(eq("followerEventTopic"), eq("{\"followerId\": 1, " +
                "\"followedUserId\": 2, \"followedProjectId\": 3}"));
    }

    @Test
    public void testPublishJsonProcessingException() throws JsonProcessingException {
        FollowerEvent event = new FollowerEvent(1L, 2L, 3L, LocalDateTime.now());

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Error") {});

        assertThrows(EventPublishingException.class, () -> followerEventPublisher.publish(event));

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    public void testPublishUnexpectedException() throws JsonProcessingException {
        FollowerEvent event = new FollowerEvent(1L, 2L, 3L, LocalDateTime.now());

        when(objectMapper.writeValueAsString(event)).thenReturn("{\"follower\": \"user1\", \"following\": \"user2\"}");

        doThrow(new RuntimeException("Redis error")).when(redisTemplate).convertAndSend(anyString(), anyString());

        assertThrows(EventPublishingException.class, () -> followerEventPublisher.publish(event));

        verify(redisTemplate).convertAndSend(eq("followerEventTopic"), eq("{\"follower\": \"user1\", " +
                "\"following\": \"user2\"}"));
    }
}
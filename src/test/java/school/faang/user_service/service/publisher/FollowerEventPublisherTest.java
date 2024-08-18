package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.FollowerEvent;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class FollowerEventPublisherTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private String channel;
    private String message;
    private FollowerEvent followerEvent;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @BeforeEach
    public void setUp() {
    channel = "channel";
    followerEvent = FollowerEvent.builder().followerId(1L).followeeId(2L)
            .subscriptionTime(LocalDateTime.of(2024, 8, 17, 0, 0)).build();
    message = followerEvent.toString();
    followerEventPublisher = new FollowerEventPublisher(redisTemplate, objectMapper, channel);
    }

    @Test
    public void publishTest() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(followerEvent)).thenReturn(message);
        Mockito.when(redisTemplate.convertAndSend(channel, message)).thenReturn(1L);
        followerEventPublisher.publish(followerEvent);
        Mockito.verify(redisTemplate, Mockito.times(1)).convertAndSend(channel, message);
        Mockito.verify(objectMapper, Mockito.times(1)).writeValueAsString(followerEvent);
    }

    @Test
    public void publishTestWithException() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(followerEvent)).thenThrow(RuntimeException.class);
        Assert.assertThrows(RuntimeException.class, () -> followerEventPublisher.publish(followerEvent));
    }
}

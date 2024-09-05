package school.faang.user_service.publisher;

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
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.publisher.goal.GoalEventPublisher;

@ExtendWith(MockitoExtension.class)
class GoalEventPublisherTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private GoalEventPublisher goalEventPublisher;

    private String channel;
    private String message;
    private GoalSetEvent goalSetEvent;


    @BeforeEach
    public void setUp() {
        channel = "channel";
        goalSetEvent = new GoalSetEvent();
        message = goalSetEvent.toString();
        goalEventPublisher = new GoalEventPublisher(redisTemplate, new ChannelTopic(channel), objectMapper);
    }

    @Test
    void publishTest() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(goalSetEvent)).thenReturn(message);
        Mockito.when(redisTemplate.convertAndSend(channel, message)).thenReturn(1L);
        goalEventPublisher.publish(goalSetEvent);
        Mockito.verify(redisTemplate, Mockito.times(1)).convertAndSend(channel, message);
        Mockito.verify(objectMapper, Mockito.times(1)).writeValueAsString(goalSetEvent);
    }

    @Test
    void publishTestWithException() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(goalSetEvent)).thenThrow(RuntimeException.class);
        Assert.assertThrows(RuntimeException.class, () -> goalEventPublisher.publish(goalSetEvent));
    }
}

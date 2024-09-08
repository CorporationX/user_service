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
import school.faang.user_service.event.ProjectFollowerEvent;

@ExtendWith(MockitoExtension.class)
class ProjectFollowerEventPublisherTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private ProjectFollowerEventPublisher projectFollowerEventPublisher;

    private String channel;
    private String message;
    private ProjectFollowerEvent projectFollowerEvent;


    @BeforeEach
    public void setUp() {
        channel = "channel";
        projectFollowerEvent = new ProjectFollowerEvent(1L,1L,1L);
        message = projectFollowerEvent.toString();
        projectFollowerEventPublisher = new ProjectFollowerEventPublisher(redisTemplate, "chanel name", objectMapper);
    }

    @Test
    void publishTest() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(projectFollowerEvent)).thenReturn(message);
        Mockito.when(redisTemplate.convertAndSend(channel, message)).thenReturn(1L);
        projectFollowerEventPublisher.publish(projectFollowerEvent);
        Mockito.verify(redisTemplate, Mockito.times(1)).convertAndSend(channel, message);
        Mockito.verify(objectMapper, Mockito.times(1)).writeValueAsString(projectFollowerEvent);
    }

    @Test
    void publishTestWithException() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(projectFollowerEvent)).thenThrow(RuntimeException.class);
        Assert.assertThrows(RuntimeException.class, () -> projectFollowerEventPublisher.publish(projectFollowerEvent));
    }
}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.RecommendationEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationSentPublisherTest {

    @Mock
    private ChannelTopic topic;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RecommendationSentPublisher recommendationSentPublisher;

    private RecommendationEventPublisher event;

    public void setUp() {
        event = RecommendationEventPublisher.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(1L)
                .receivedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testPublish_success() throws JsonProcessingException {
        String message = "test case";
        String channel = "test topic";

        when(objectMapper.writeValueAsString(event)).thenReturn(message);
        when(topic.getTopic()).thenReturn(channel);

        recommendationSentPublisher.publish(event);

        verify(redisTemplate).convertAndSend(channel, message);
        verify(objectMapper).writeValueAsString(event);
    }

    @Test
    public void testPublish_failed() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        recommendationSentPublisher.publish(event);

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
        verify(objectMapper).writeValueAsString(event);
    }
}

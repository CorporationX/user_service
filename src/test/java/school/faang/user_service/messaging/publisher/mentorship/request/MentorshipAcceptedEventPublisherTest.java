package school.faang.user_service.messaging.publisher.mentorship.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.RedisEvent;
import school.faang.user_service.exception.event.EventPublishingException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipAcceptedEventPublisherTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic topic;
    @InjectMocks
    private MentorshipAcceptedEventPublisher publisher;

    @Test
    void publishSuccessfully() throws IOException {
        RedisEvent event = new RedisEvent();
        String serializedEvent = "{\"eventId\":\"123\",\"eventType\":\"MentorshipAccepted\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(serializedEvent);
        when(topic.getTopic()).thenReturn("mentorship-events");

        publisher.publish(event);

        verify(objectMapper).writeValueAsString(event);
        verify(redisTemplate).convertAndSend("mentorship-events", serializedEvent);
    }

    @Test
    void publishThrowsEventPublishingException() throws IOException {
        RedisEvent event = new RedisEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        assertThrows(EventPublishingException.class, () -> publisher.publish(event));

        verify(objectMapper).writeValueAsString(event);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}
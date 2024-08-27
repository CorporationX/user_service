package school.faang.user_service.messaging.publisher.mentorship.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.mentorship.request.MentorshipOfferedEvent;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipOfferedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic mentorshipOfferedTopic;
    @InjectMocks
    private MentorshipOfferedEventPublisher  mentorshipOfferedEventPublisher;
    private MentorshipOfferedEvent event;
    @BeforeEach
    void setUp() {
        event = new MentorshipOfferedEvent();
    }

    @Test
    void testPublishSuccess() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn("message");
        when(mentorshipOfferedTopic.getTopic()).thenReturn("mentorship-events");

        mentorshipOfferedEventPublisher.publish(event);

        verify(objectMapper).writeValueAsString(event);
        verify(redisTemplate).convertAndSend("mentorship-events", "message");
    }

    @Test
    void testPublishJsonException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        mentorshipOfferedEventPublisher.publish(event);

        verify(objectMapper).writeValueAsString(event);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    void testPublishException() throws Exception {
        when(objectMapper.writeValueAsString(event)).thenThrow(new RuntimeException("Serialization error") {});

        mentorshipOfferedEventPublisher.publish(event);

        verify(objectMapper).writeValueAsString(event);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}

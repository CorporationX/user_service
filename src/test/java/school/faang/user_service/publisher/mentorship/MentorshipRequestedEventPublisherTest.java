package school.faang.user_service.publisher.mentorship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.mentorship.MentorshipRequestedEvent;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;
    private MentorshipRequestedEvent mentorshipRequestedEvent;
    String message = "test message";

    @BeforeEach
    void init() {
        LocalDateTime localDateTime = LocalDateTime.now();
        mentorshipRequestedEvent = MentorshipRequestedEvent.builder()
                .requesterId(1L)
                .receiverId(2L)
                .timestamp(localDateTime)
                .build();
    }

    @Test
    @DisplayName("writeValueAsString")
    public void testWriteValueAsString() throws Exception {
        when(objectMapper.writeValueAsString(mentorshipRequestedEvent)).thenReturn(message);

        mentorshipRequestedEventPublisher.publish(mentorshipRequestedEvent);

        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), message);
        verify(objectMapper).writeValueAsString(mentorshipRequestedEvent);
    }

    @Test
    @DisplayName("getTopic")
    void testGetTopic() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(MentorshipRequestedEvent.class)))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn("test-topic");

        mentorshipRequestedEventPublisher.publish(mentorshipRequestedEvent);

        verify(objectMapper, Mockito.times(1))
                .writeValueAsString(Mockito.any(MentorshipRequestedEvent.class));
        verify(redisTemplate, Mockito.times(1))
                .convertAndSend("test-topic", message);
    }

    @Test
    @DisplayName("convertAndSend")
    void testConvertAndSend() throws Exception {
        when(objectMapper.writeValueAsString(Mockito.any(MentorshipRequestedEvent.class)))
                .thenReturn(message);
        when(channelTopic.getTopic())
                .thenReturn("test-topic");
        when(redisTemplate.convertAndSend(anyString(), anyString()))
                .thenReturn(anyLong());

        mentorshipRequestedEventPublisher.publish(mentorshipRequestedEvent);

        verify(objectMapper, Mockito.times(1))
                .writeValueAsString(Mockito.any(MentorshipRequestedEvent.class));
        verify(redisTemplate, Mockito.times(1))
                .convertAndSend("test-topic", message);
        verify(redisTemplate, times(1))
                .convertAndSend(anyString(), anyString());
    }
}
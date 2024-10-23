package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.exception.EventPublishingException;
import school.faang.user_service.model.event.ProfileViewEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileViewEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private ProfileViewEventPublisher eventPublisher;

    private ProfileViewEvent testEvent;

    @BeforeEach
    public void setUp() {
        testEvent = new ProfileViewEvent(1L, 2L, LocalDateTime.now());
    }

    @Test
    @DisplayName("Should publish ProfileViewEvent successfully")
    public void testPublish_Success() throws JsonProcessingException {
        String expectedMessage = "serialized event";
        when(objectMapper.writeValueAsString(testEvent)).thenReturn(expectedMessage);
        when(channelTopic.getTopic()).thenReturn("testTopic");

        eventPublisher.publish(testEvent);

        verify(redisTemplate, times(1)).convertAndSend(eq("testTopic"), eq(expectedMessage));
    }

    @Test
    @DisplayName("Should throw EventPublishingException when serialization fails")
    public void testPublish_SerializationFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(testEvent)).thenThrow(new JsonProcessingException("Test") {});

        assertThrows(EventPublishingException.class, () -> eventPublisher.publish(testEvent));

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw EventPublishingException for unexpected error")
    public void testPublish_UnexpectedError() {
        when(channelTopic.getTopic()).thenReturn("testTopic");
        doThrow(new RuntimeException("Test")).when(redisTemplate).convertAndSend(anyString(), anyString());

        assertThrows(EventPublishingException.class, () -> eventPublisher.publish(testEvent));
    }
}

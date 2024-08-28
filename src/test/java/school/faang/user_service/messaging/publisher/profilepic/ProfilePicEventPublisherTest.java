package school.faang.user_service.messaging.publisher.profilepic;

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
import school.faang.user_service.event.profilepic.ProfilePicEvent;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProfilePicEventPublisherTest {
    @Mock
    private  RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic profilePicTopic;
    @InjectMocks
    private ProfilePicEventPublisher profilePicEventPublisher;
    private ProfilePicEvent profilePicEvent;

    @BeforeEach
    void setUp() {
        profilePicEvent = new ProfilePicEvent();
    }

    @Test
    void testPublishSuccess() throws JsonProcessingException {
        when(profilePicTopic.getTopic()).thenReturn("testTopic");

        String message = "testMessage";

        when(objectMapper.writeValueAsString(any(ProfilePicEvent.class))).thenReturn(message);

        profilePicEventPublisher.publish(profilePicEvent);

        verify(objectMapper).writeValueAsString(profilePicEvent);
        verify(redisTemplate).convertAndSend("testTopic", message);
        verifyNoMoreInteractions(objectMapper, redisTemplate);
    }

    @Test
    void testPublishJsonProcessingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(profilePicEvent))
                .thenThrow(new JsonProcessingException("error") {});

        profilePicEventPublisher.publish(profilePicEvent);

        verify(objectMapper).writeValueAsString(profilePicEvent);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    void testPublishUnexpectedException() throws Exception {
        when(objectMapper.writeValueAsString(profilePicEvent))
                .thenThrow(new RuntimeException("Unexpected error") {});

        profilePicEventPublisher.publish(profilePicEvent);

        verify(objectMapper).writeValueAsString(profilePicEvent);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

}

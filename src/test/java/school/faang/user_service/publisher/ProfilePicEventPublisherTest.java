package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.userprofile.ProfilePicEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of ProfilePicEventPublisherTest")
public class ProfilePicEventPublisherTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    @Qualifier("profilePicChannel")
    private ChannelTopic channelTopic;

    @InjectMocks
    private ProfilePicEventPublisher publisher;

    @Value("${spring.data.redis.channel.profile-pic}")
    private String channelTitle;

    private ProfilePicEvent event;

    @BeforeEach
    public void setUp() {
        event = ProfilePicEvent.builder()
                .userId(1L)
                .picLink("http://example.com/pic.jpg")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("publish - serialization failed")
    public void testPublishWithFailedSerialized() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonMappingException(null, "Failed to serialize"));

        Exception exception = assertThrows(SerializationFailedException.class, () -> publisher.publish(event));

        assertEquals("Event serialization failed", exception.getMessage());
    }

    @Test
    @DisplayName("publish - successfully")
    public void testPublishSuccessfully() throws JsonProcessingException {
        String json = "json";
        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        when(channelTopic.getTopic()).thenReturn(channelTitle);

        publisher.publish(event);

        verify(stringRedisTemplate, times(1)).convertAndSend(channelTitle, json);
    }
}

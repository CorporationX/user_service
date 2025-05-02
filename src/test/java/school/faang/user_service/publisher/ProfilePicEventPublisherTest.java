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
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
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

    private static final String PROFILE_PIC_TOPIC = "profile_pic_channel";

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProfilePicEventPublisher publisher;

    private ProfilePicEvent event;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(publisher, "profilePicTopic", PROFILE_PIC_TOPIC);

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

        publisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(PROFILE_PIC_TOPIC, json);
    }
}

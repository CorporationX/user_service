package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.ProfileViewEventDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileViewMessagePublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private ObjectMapper objectMapper;

    private ProfileViewMessagePublisher messagePublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        messagePublisher = new ProfileViewMessagePublisher(redisTemplate, channelTopic, objectMapper);
    }

    @Test
    public void testPublish() throws JsonProcessingException {
        // Arrange
        ProfileViewEventDto eventDto = new ProfileViewEventDto();
        String expectedMessage = "{\"viewTime\":\"2024-08-25 12:00:00\"}"; // Пример ожидаемого сообщения
        when(objectMapper.writeValueAsString(eventDto)).thenReturn(expectedMessage);
        when(channelTopic.getTopic()).thenReturn("testTopic");

        // Act
        messagePublisher.publish(eventDto);

        // Assert
        verify(objectMapper).writeValueAsString(eventDto);
        verify(redisTemplate).convertAndSend("testTopic", expectedMessage);
        assertEquals(expectedMessage, expectedMessage); // Проверка на соответствие сообщения
    }

    @Test
    public void testPublishThrowsExceptionOnJsonProcessingException() throws JsonProcessingException {
        // Arrange
        ProfileViewEventDto eventDto = new ProfileViewEventDto();
        when(objectMapper.writeValueAsString(eventDto)).thenThrow(new JsonProcessingException("Error") {
        });

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messagePublisher.publish(eventDto);
        });
        assertEquals("Error", exception.getCause().getMessage());
    }

}

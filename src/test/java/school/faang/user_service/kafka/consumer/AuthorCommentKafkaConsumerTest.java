package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import school.faang.user_service.model.event.kafka.AuthorCommentKafkaEvent;
import school.faang.user_service.service.RedisUserService;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorCommentKafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisUserService redisUserService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private AuthorCommentKafkaConsumer consumer;

    @Test
    void testProcessEvent_Success() {
        // Arrange
        AuthorCommentKafkaEvent event = new AuthorCommentKafkaEvent(1L);

        // Act
        consumer.processEvent(event);

        // Assert
        verify(redisUserService, times(1)).saveUser(event.getCommentAuthorId());
    }

    @Test
    void testOnMessage_Success() throws JsonProcessingException {
        // Arrange
        String jsonEvent = """
                {
                    "commentAuthorId": 1
                }
                """;

        AuthorCommentKafkaEvent event = new AuthorCommentKafkaEvent(1L);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", jsonEvent);

        when(objectMapper.readValue(jsonEvent, AuthorCommentKafkaEvent.class)).thenReturn(event);

        // Act
        consumer.onMessage(record, acknowledgment);

        // Assert
        verify(redisUserService, times(1)).saveUser(event.getCommentAuthorId());
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testOnMessage_ErrorHandling() throws JsonProcessingException {
        // Arrange
        String invalidJsonEvent = "{invalid json}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", invalidJsonEvent);

        when(objectMapper.readValue(invalidJsonEvent, AuthorCommentKafkaEvent.class)).thenThrow(JsonProcessingException.class);

        // Act & Assert
        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                    consumer.onMessage(record, acknowledgment);
                });

        verify(redisUserService, never()).saveUser(anyLong());
        verify(acknowledgment, never()).acknowledge();
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("Failed to deserialize user made comment event"));
    }
}

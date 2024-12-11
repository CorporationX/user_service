package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.model.event.kafka.PostPublishedKafkaEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FeedHeatKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FeedHeatKafkaProducer producer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize producer with a static topic for testing
        producer = new FeedHeatKafkaProducer(kafkaTemplate, objectMapper) {
            @Override
            protected String getTopic() {
                return "feed-heat-topic";
            }
        };
    }

    @Test
    void testSendEvent_Success() throws JsonProcessingException {
        // Arrange
        PostPublishedKafkaEvent event = new PostPublishedKafkaEvent(1L, List.of(2L, 3L), LocalDateTime.parse("2024-12-10T15:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        String eventJson = "{\"postId\":1,\"followerIds\":[2,3],\"publishedAt\":\"2024-12-10T15:30:00\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(eventJson);

        // Act
        producer.sendEvent(event);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertEquals("feed-heat-topic", topicCaptor.getValue());
        assertEquals(eventJson, messageCaptor.getValue());
    }

    @Test
    void testSendEvent_Failure() throws JsonProcessingException {
        // Arrange
        PostPublishedKafkaEvent event = new PostPublishedKafkaEvent(1L, List.of(2L, 3L), LocalDateTime.parse("2024-12-10T15:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> producer.sendEvent(event));
        assertEquals("Failed to serialize event to JSON", exception.getMessage());

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}

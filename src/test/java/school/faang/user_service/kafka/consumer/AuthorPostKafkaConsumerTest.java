package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.support.Acknowledgment;
import school.faang.user_service.kafka.producer.PostKafkaProducer;
import school.faang.user_service.model.event.kafka.AuthorPostKafkaEvent;
import school.faang.user_service.model.event.kafka.PostPublishedKafkaEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.RedisUserService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorPostKafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisUserService redisUserService;

    @Mock
    private PostKafkaProducer postKafkaProducer;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private AuthorPostKafkaConsumer consumer;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        // Set followerBatchSize via reflection
        Field batchSizeField = AuthorPostKafkaConsumer.class.getDeclaredField("followerBatchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(consumer, 1000);
    }

    @Test
    void testProcessEvent_Success() {
        // Arrange
        Long authorId = 1L;
        Long postId = 2L;
        LocalDateTime publishedAt = LocalDateTime.now();
        AuthorPostKafkaEvent event = new AuthorPostKafkaEvent(postId, authorId, publishedAt);

        Page<Long> followersPage1 = new PageImpl<>(List.of(3L, 4L));
        Page<Long> emptyPage = Page.empty();

        when(userRepository.findUnbannedFollowerIdsByUserId(eq(authorId), any(PageRequest.class)))
                .thenReturn(followersPage1, emptyPage);

        // Act
        consumer.processEvent(event);

        // Assert
        verify(redisUserService).saveUser(authorId);

        ArgumentCaptor<PostPublishedKafkaEvent> eventCaptor = ArgumentCaptor.forClass(PostPublishedKafkaEvent.class);
        verify(postKafkaProducer, times(1)).sendEvent(eventCaptor.capture());

        PostPublishedKafkaEvent capturedEvent = eventCaptor.getValue();
        assertEquals(postId, capturedEvent.getPostId());
        assertEquals(List.of(3L, 4L), capturedEvent.getFollowerIds());
        assertEquals(publishedAt, capturedEvent.getPublishedAt());
    }

    @Test
    void testOnMessage_Success() throws JsonProcessingException {
        // Arrange
        String jsonEvent = """
                {
                    "postId": 2,
                    "authorId": 1,
                    "publishedAt": "2024-12-10T15:30:00"
                }
                """;
        AuthorPostKafkaEvent event = new AuthorPostKafkaEvent(2L, 1L, LocalDateTime.parse("2024-12-10T15:30:00"));
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", jsonEvent);

        when(objectMapper.readValue(jsonEvent, AuthorPostKafkaEvent.class)).thenReturn(event);

        // Act
        consumer.onMessage(record, acknowledgment);

        // Assert
        verify(redisUserService).saveUser(event.getAuthorId());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void testSendPostPublishedKafkaEvents_NoFollowers() {
        // Arrange
        Long authorId = 1L;
        Long postId = 2L;
        LocalDateTime publishedAt = LocalDateTime.now();
        AuthorPostKafkaEvent event = new AuthorPostKafkaEvent(postId, authorId, publishedAt);

        when(userRepository.findUnbannedFollowerIdsByUserId(eq(authorId), any(PageRequest.class))).thenReturn(Page.empty());

        // Act
        consumer.processEvent(event);

        // Assert
        verify(postKafkaProducer, never()).sendEvent(any());
    }

    @Test
    void testHandleError() {
        // Arrange
        String invalidJson = "{ invalid json }";
        Exception exception = new RuntimeException("Deserialization error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                consumer.handleError(invalidJson, exception, acknowledgment));

        assertEquals(
                "Failed to deserialize user made post event: { invalid json } and add him to user map in redis",
                thrown.getMessage());
    }
}

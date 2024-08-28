package school.faang.user_service.messaging.publisher.recommendation;

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
import school.faang.user_service.event.recommendation.RecommendationEvent;
import school.faang.user_service.exception.ExceptionMessages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic recommendationTopic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecommendationEventPublisher recommendationEventPublisher;

    private RecommendationEvent recommendationEvent;

    @BeforeEach
    void setUp() {
        recommendationEvent = new RecommendationEvent();
    }

    @Test
    void testPublishSuccess() throws JsonProcessingException {
        when(recommendationTopic.getTopic()).thenReturn("testTopic");

        String message = "testMessage";

        when(objectMapper.writeValueAsString(recommendationEvent)).thenReturn(message);

        recommendationEventPublisher.publish(recommendationEvent);

        verify(objectMapper).writeValueAsString(recommendationEvent);
        verify(redisTemplate).convertAndSend("testTopic", message);
        verifyNoMoreInteractions(objectMapper, redisTemplate);
    }

    @Test
    void testPublishJsonProcessingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(recommendationEvent)).thenThrow(new JsonProcessingException("error") {});

        recommendationEventPublisher.publish(recommendationEvent);

        verify(objectMapper).writeValueAsString(recommendationEvent);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    void testPublishUnexpectedException() throws JsonProcessingException {
        when(recommendationTopic.getTopic()).thenReturn("testTopic");

        when(objectMapper.writeValueAsString(recommendationEvent)).thenReturn("testMessage");
        doThrow(new RuntimeException("Unexpected error")).when(redisTemplate).convertAndSend(anyString(), anyString());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationEventPublisher.publish(recommendationEvent));

        verify(objectMapper).writeValueAsString(recommendationEvent);
        verify(redisTemplate).convertAndSend(anyString(), anyString());
        assertEquals(ExceptionMessages.UNEXPECTED_ERROR + "Unexpected error", exception.getMessage());
    }
}


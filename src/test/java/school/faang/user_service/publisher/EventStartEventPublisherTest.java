package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.EventStartEvent;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventStartEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventStartEventPublisher eventStartEventPublisher;

    private final String topic = "event-start-topic";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventStartEventPublisher = new EventStartEventPublisher(redisTemplate, objectMapper, topic);
    }

    @Test
    void testPublish() throws JsonProcessingException {
        EventStartEvent event = new EventStartEvent(1L, List.of(2L, 3L));
        String eventJson = "{\"eventId\":1,\"participants\":[2,3]}";

        when(objectMapper.writeValueAsString(event)).thenReturn(eventJson);

        eventStartEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(eq(topic), eq(eventJson));
    }
}


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
import school.faang.user_service.dto.event.SearchAppearanceEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchAppearanceEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SearchAppearanceEvent searchAppearanceEvent;
    @InjectMocks
    private SearchAppearanceEventPublisher publisher;

    private String json;

    @BeforeEach
    public void setUp() {
        String topic = "profile_search_channel";
        publisher = new SearchAppearanceEventPublisher(redisTemplate, objectMapper, topic);
        json = "JSON";
    }

    @Test
    @DisplayName("Checking the correctness works of the method")
    public void testPublish() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(searchAppearanceEvent)).thenReturn(json);

        publisher.publish(searchAppearanceEvent);

        verify(objectMapper).writeValueAsString(searchAppearanceEvent);
        verify(redisTemplate).convertAndSend(anyString(), eq(json));
    }

    @Test
    @DisplayName("Checking that the method throws an exception")
    public void testPublish_FailedSerialization() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(searchAppearanceEvent)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> publisher.publish(searchAppearanceEvent));

        verify(objectMapper).writeValueAsString(searchAppearanceEvent);
    }
}
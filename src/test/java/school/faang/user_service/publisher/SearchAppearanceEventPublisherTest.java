package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.analytics.SearchAppearanceEventDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchAppearanceEventPublisherTest {

    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private SearchAppearanceEventDto searchAppearanceEventDto;
    @Mock
    private ObjectMapper objectMapper;
    private String json;

    @BeforeEach
    void setUp() {
        String topic = "search-appearance-channel";
        searchAppearanceEventPublisher = new SearchAppearanceEventPublisher(objectMapper, redisTemplate, topic);
        json = "EXPECTED_JSON";

    }

    @Test
    void testMethodPublish() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(searchAppearanceEventDto)).thenReturn(json);

        searchAppearanceEventPublisher.publish(searchAppearanceEventDto);

        verify(redisTemplate).convertAndSend(anyString(), eq(json));
        verify(objectMapper).writeValueAsString(searchAppearanceEventDto);
    }

    @Test
    void testToJson_FailedSerialization() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(searchAppearanceEventDto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> searchAppearanceEventPublisher.publish(searchAppearanceEventDto));

        verify(objectMapper).writeValueAsString(searchAppearanceEventDto);

    }
}
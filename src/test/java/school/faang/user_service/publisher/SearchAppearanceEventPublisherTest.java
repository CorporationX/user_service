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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    private Object object;

    @BeforeEach
    void setUp() {
        searchAppearanceEventPublisher = new SearchAppearanceEventPublisher(redisTemplate, objectMapper);
        searchAppearanceEventPublisher.setSearchAppearanceTopic("search-appearance-channel");
        json = "EXPECTED_JSON";
        object = new Object();
    }

    @Test
    void testMethodPublish() {
        when(searchAppearanceEventPublisher.toJson(searchAppearanceEventDto)).thenReturn(json);

        searchAppearanceEventPublisher.publish(searchAppearanceEventDto);

        verify(redisTemplate).convertAndSend(anyString(), eq(json));
    }

    @Test
    void testToJson_SuccessfulSerialization() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(object)).thenReturn(json);

        String resultJson = searchAppearanceEventPublisher.toJson(object);

        verify(objectMapper).writeValueAsString(object);
        assertEquals(json, resultJson);
    }

    @Test
    void testToJson_FailedSerialization() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(object)).thenThrow(JsonProcessingException.class);

        String resultJson = searchAppearanceEventPublisher.toJson(object);

        verify(objectMapper).writeValueAsString(object);
        assertNull(resultJson);
    }
}
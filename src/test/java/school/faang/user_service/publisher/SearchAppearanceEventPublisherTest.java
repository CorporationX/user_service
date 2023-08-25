package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.analytics.SearchAppearanceEventDto;
import school.faang.user_service.mapper.JsonObjectMapper;

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
    private JsonObjectMapper jsonObjectMapper;
    @Mock
    private SearchAppearanceEventDto searchAppearanceEventDto;
    private String json;

    @BeforeEach
    void setUp() {
        searchAppearanceEventPublisher = new SearchAppearanceEventPublisher(redisTemplate, jsonObjectMapper);
        searchAppearanceEventPublisher.setSearchAppearanceTopic("search-appearance-channel");
        json = "EXPECTED_JSON";
    }

    @Test
    void testMethodPublish() {
        when(jsonObjectMapper.toJson(searchAppearanceEventDto)).thenReturn(json);

        searchAppearanceEventPublisher.publish(searchAppearanceEventDto);

        verify(jsonObjectMapper).toJson(searchAppearanceEventDto);
        verify(redisTemplate).convertAndSend(anyString(), eq(json));
    }
}
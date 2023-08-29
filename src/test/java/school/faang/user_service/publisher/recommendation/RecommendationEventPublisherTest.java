package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationEventPublisherTest {
    @InjectMocks
    private RecommendationEventPublisher publisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper jsonMapper;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(jsonMapper.writeValueAsString(any())).thenReturn("json");
        ReflectionTestUtils.setField(publisher, "channel", "channel");
    }

    @Test
    void publish_shouldInvokeMapperWriteValueAsStringMethod() throws JsonProcessingException {
        publisher.publish(new RecommendationEventDto());
        verify(jsonMapper).writeValueAsString(any());
    }

    @Test
    void publish_shouldInvokeRedisTemplateConvertAndSendMethod() {
        publisher.publish(new RecommendationEventDto());
        verify(redisTemplate).convertAndSend("channel", "json");
    }
}
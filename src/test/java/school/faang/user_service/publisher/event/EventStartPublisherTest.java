package school.faang.user_service.publisher.event;

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
import school.faang.user_service.dto.event.EventStartDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStartPublisherTest {
    @InjectMocks
    private EventStartPublisher eventStartPublisher;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    private EventStartDto eventStartDto;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        eventStartDto = EventStartDto.builder().build();
        when(objectMapper.writeValueAsString(any())).thenReturn("json");
        ReflectionTestUtils.setField(eventStartPublisher, "channel", "channel");
    }


    @Test
    void publish_shouldInvokeWriteValueAsStringMethod() throws JsonProcessingException {
        eventStartPublisher.publish(eventStartDto);
        verify(objectMapper).writeValueAsString(eventStartDto);
    }

    @Test
    void publish_shouldInvokeConvertAndSendMethod() {
        eventStartPublisher.publish(eventStartDto);
        verify(redisTemplate).convertAndSend("channel", "json");
    }
}
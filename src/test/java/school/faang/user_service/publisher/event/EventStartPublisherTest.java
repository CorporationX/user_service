package school.faang.user_service.publisher.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventStartDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStartPublisherTest {
    @Mock
    ObjectMapper objectMapper;
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    EventStartPublisher eventStartPublisher;
    @Mock
    private EventStartDto eventStartDto;


    @Test
    void publish_shouldInvokeWriteValueAsStringMethod() throws JsonProcessingException {
        eventStartPublisher.publish(eventStartDto);
        verify(objectMapper).writeValueAsString(eventStartDto);
    }

    @Test
    void publish_shouldInvokeConvertAndSendMethod() throws JsonProcessingException {
        ReflectionTestUtils.setField(eventStartPublisher, "channel", "some_channel");
        when(objectMapper.writeValueAsString(eventStartDto)).thenReturn("some_json");

        eventStartPublisher.publish(eventStartDto);

        verify(redisTemplate).convertAndSend("some_channel", "some_json");
    }
}
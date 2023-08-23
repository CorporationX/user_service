package school.faang.user_service.publisher.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.mapper.JsonMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStartPublisherTest {
    @Mock
    private JsonMapper<EventStartDto> jsonMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    private EventStartPublisher eventStartPublisher;
    @Mock
    private EventStartDto eventStartDto;


    @Test
    void publish_shouldInvokeWriteValueAsStringMethod() {
        eventStartPublisher.publish(eventStartDto);
        verify(jsonMapper).toJson(eventStartDto);
    }

    @Test
    void publish_shouldInvokeConvertAndSendMethod() {
        ReflectionTestUtils.setField(eventStartPublisher, "channel", "some_channel");
        when(jsonMapper.toJson(eventStartDto)).thenReturn("some_json");

        eventStartPublisher.publish(eventStartDto);

        verify(redisTemplate).convertAndSend("some_channel", "some_json");
    }
}
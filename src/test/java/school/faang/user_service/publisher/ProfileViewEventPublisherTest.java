package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfileViewEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(value = {MockitoExtension.class})
public class ProfileViewEventPublisherTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(profileViewEventPublisher, "profileViewChannelName", "testChannel");
    }

    @Test
    public void testPublish() throws JsonProcessingException {
        ProfileViewEvent event = new ProfileViewEvent(1L, 2L, null);
        String channel = "testChannel";
        String json = "{\n" +
                "  \"userId\": 1,\n" +
                "  \"profileViewedId\": 2,\n" +
                "  \"date\": null\n" +
                "}";

        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        profileViewEventPublisher.publish(event);
        verify(redisMessagePublisher).publish(channel, json);
    }

}

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
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfileViewEvent;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(value = {MockitoExtension.class})
public class ProfileViewEventPublisherTest {

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(profileViewEventPublisher, "profileViewChannelName", "testChannel");
    }

    @Test
    public void testPublish() {
        String channel = "testChannel";
        when(userContext.getUserId()).thenReturn(1L);
        profileViewEventPublisher.publishProfileViewEvent(2L);
        verify(redisMessagePublisher).publish(eq(channel), any());
    }

}

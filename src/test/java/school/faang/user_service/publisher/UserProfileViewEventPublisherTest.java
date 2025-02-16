package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.events.UserProfileViewEvent;

@ExtendWith(MockitoExtension.class)
class UserProfileViewEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    UserProfileViewEventPublisher userProfileViewEventPublisher;

    @Test
    void publish() {
        Long userId = 1L;
        Long visitorId = 2L;
        UserProfileViewEvent message = new UserProfileViewEvent(userId, visitorId, null);

        userProfileViewEventPublisher.publish(message);

        Mockito.verify(redisTemplate, Mockito.times(1))
                .convertAndSend(Mockito.any(), Mockito.anyString());
    }
}
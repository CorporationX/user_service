package school.faang.user_service.publisher;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.ProfileViewEventDto;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
public class ProfileViewEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;

    @Test
    @DisplayName("Send Event Test")
    public void sendTestEvent() {
        ProfileViewEventDto evt = ProfileViewEventDto.builder().build();
        profileViewEventPublisher.publish(evt);
        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), evt);
    }
}

package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.event.NiceGuyEvent;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class NiceGuyEventPublisherTest {
    @Value("${spring.data.redis.channels.nice-guy-achievement-channel.name}")
    String niceGuyAchievementChannelName;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private NiceGuyPublisher niceGuyPublisher;
    @BeforeEach
    void setUp() {
        when(channelTopic.getTopic()).thenReturn(niceGuyAchievementChannelName);
    }
    @Test
//    @DisplayName("Send Event Test")
    public void sendTestEvent() {
        NiceGuyEvent evt = NiceGuyEvent.builder().build();
        niceGuyPublisher.publish(evt);
        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), evt);
    }
}

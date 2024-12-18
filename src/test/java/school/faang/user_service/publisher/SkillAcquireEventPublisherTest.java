package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.SkillAcquireEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillAcquireEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @InjectMocks
    private SkillAcquireEventPublisher skillAcquireEventPublisher;

    @Test
    void testPublishSuccess() {
        SkillAcquireEvent skillAcquireEvent = SkillAcquireEvent.builder()
                .authorId(1L)
                .receiverId(2L)
                .skillId(1L)
                .build();

        String channelName = "skill_channel";
        RedisProperties.Channel channel = new RedisProperties.Channel();
        channel.setSkillChannel(channelName);

        when(redisProperties.getChannel()).thenReturn(channel);
        skillAcquireEventPublisher.publish(skillAcquireEvent);

        verify(redisTemplate, times(1)).convertAndSend(channelName, skillAcquireEvent);

    }
}

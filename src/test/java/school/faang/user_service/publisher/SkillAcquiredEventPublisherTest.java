package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.SkillAcquiredEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkillAcquiredEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic skillAcquiredTopic;

    @InjectMocks
    private SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @Test
    void publish_isOk() {
        // given
        var skillAcquiredEvent = SkillAcquiredEvent.builder().build();
        // when
        skillAcquiredEventPublisher.publish(skillAcquiredEvent);
        // then
        verify(redisTemplate).convertAndSend(skillAcquiredTopic.getTopic(), skillAcquiredEvent);
    }
}

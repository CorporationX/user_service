package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SkillAcquiredEvent;

@Component
@RequiredArgsConstructor
public class SkillAcquiredEventPublisher implements MessagePublisher<SkillAcquiredEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic skillTopic;

    @Override
    public void publish(SkillAcquiredEvent event) {
        redisTemplate.convertAndSend(skillTopic.getTopic(), event);
    }
}

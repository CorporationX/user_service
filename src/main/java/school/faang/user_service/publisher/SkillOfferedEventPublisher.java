package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.SkillOfferedEvent;

@Component
@RequiredArgsConstructor
public class SkillOfferedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic skillOfferedTopic;

    public void publish(SkillOfferedEvent event) {
        redisTemplate.convertAndSend(skillOfferedTopic.getTopic(), event);
    }
}

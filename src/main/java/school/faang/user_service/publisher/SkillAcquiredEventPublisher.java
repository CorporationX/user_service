package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SkillAcquiredEvent;

@Component

public class SkillAcquiredEventPublisher extends AbstractMessagePublisher<SkillAcquiredEvent> {

    public SkillAcquiredEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    private final ChannelTopic skillTopic;

    public void publish(SkillAcquiredEvent event) {
        convertAndSend(skillTopic.getTopic(), event);
    }
}

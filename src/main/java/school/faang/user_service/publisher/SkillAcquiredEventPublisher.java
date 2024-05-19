package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SkillAcquiredEvent;

@Component

public class SkillAcquiredEventPublisher extends AbstractMessagePublisher<SkillAcquiredEvent> {

    public SkillAcquiredEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }
    @Autowired
    private ChannelTopic skillTopic;

    @Override
    public void publish(SkillAcquiredEvent event) {
        convertAndSend(skillTopic.getTopic(), event);
    }
}

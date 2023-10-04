package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillAcquiredEvent;

@Component
public class SkillAcquiredEventPublisher extends AbstractEventPublisher<SkillAcquiredEvent> {

    @Autowired
    public SkillAcquiredEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                       @Value("${spring.data.redis.channels.skill_channel}") String channelTopicName) {
        super(redisTemplate, objectMapper, channelTopicName);
    }

    public void publish(SkillAcquiredEvent event){
        publishInTopic(event);
    }
}

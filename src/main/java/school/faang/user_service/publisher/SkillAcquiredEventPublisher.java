package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillAcquiredEventDto;

@Component
public class SkillAcquiredEventPublisher extends AbstractPublisher<SkillAcquiredEventDto> {

    public SkillAcquiredEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                       ObjectMapper jsonMapper,
                                       @Value("${spring.data.redis.channels.skill_channel.name}") String channel) {
        super(redisTemplate, jsonMapper, channel);
    }
}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventSkillOfferedDto;

@Component
public class EventSkillOfferedPublisher extends AbstractPublisher<EventSkillOfferedDto> {
    public EventSkillOfferedPublisher(RedisTemplate<String, Object> redisTemplate,
                                      ObjectMapper jsonMapper,
                                      @Value("${spring.data.redis.channels.skill-offered-channel.name}") String channel) {
        super(redisTemplate, jsonMapper, channel);

        if (channel == null || channel.isEmpty() || !"skill-offered-channel".equals(channel)) {
            throw new IllegalArgumentException("Invalid channel name");
        }
    }
}
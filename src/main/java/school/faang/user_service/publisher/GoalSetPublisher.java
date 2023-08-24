package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.GoalSetEventDto;

@Component
public class GoalSetPublisher extends EventPublisher<GoalSetEventDto> {
    @Value("${spring.data.redis.channels.goal_set_channel.name}")
    private String channel;

    public GoalSetPublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String getChannel() {
        return channel;
    }
}

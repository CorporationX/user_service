package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.FollowerEvent;

@Component
public class FollowEventPublisher extends AbstractEventPublisher<FollowerEvent> {
    public FollowEventPublisher(RedisTemplate<String, Object> redisTemplate, Topic userFollowTopic,
                                ObjectMapper objectMapper) {
        super(redisTemplate, userFollowTopic, objectMapper);
    }
}

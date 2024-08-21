package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Component
public class FollowerEventPublisher extends AbstractPublisher<FollowerEvent>{

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${spring.data.redis.channels.follower_view.name}") String channel) {
        super(redisTemplate, objectMapper, channel);
    }
}

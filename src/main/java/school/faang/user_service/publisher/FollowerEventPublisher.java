package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEvent> {

    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerChannel;

    public FollowerEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    public void publish(FollowerEvent followerEvent) {
        convertAndSend(followerEvent, followerChannel);
    }
}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.follower.FollowerEventDto;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEventDto> {

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String followerEvent;

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    public void publish(FollowerEventDto event) {
        convertAndSend(event, followerEvent);
    }
}

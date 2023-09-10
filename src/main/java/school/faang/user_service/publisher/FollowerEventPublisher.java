package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.redis.FollowerEventDto;

public class FollowerEventPublisher extends EventPublisher<FollowerEventDto> {
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String channel;

    public FollowerEventPublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String getChannel() {
        return channel;
    }
}

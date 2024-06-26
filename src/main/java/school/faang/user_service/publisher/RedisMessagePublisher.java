package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {


    private final RedisTemplate<String, Object> redisTemplate;

    public void publishMessage(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }
}

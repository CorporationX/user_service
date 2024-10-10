package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;

    public void convertAndSend(T event, String channelTopic) {
        redisTemplate.convertAndSend(channelTopic, event);
        log.info("Success serializing object to JSON");
    }

    public abstract void publish(T event);
}

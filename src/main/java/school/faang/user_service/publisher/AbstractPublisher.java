package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.mapper.JsonMapper;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonMapper<T> jsonMapper;
    private final String channel;

    public void publish(T object) {
        redisTemplate.convertAndSend(channel, jsonMapper.toJson(object));
        log.info("Published new recommendation event: {}", object);
    }
}

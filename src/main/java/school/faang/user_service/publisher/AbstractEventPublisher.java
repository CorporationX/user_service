package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher<T> {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public void convertAndSend(T event, String channelTopic) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic, json);
        } catch (JsonProcessingException e) {
            log.error("Json processing exception", e);
            throw new RuntimeException("Json processing exception");
        }
    }

    public abstract void publish(T event);
}

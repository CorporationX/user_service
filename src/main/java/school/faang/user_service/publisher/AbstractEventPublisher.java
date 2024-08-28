package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
@Slf4j
public class AbstractEventPublisher<T> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ObjectMapper objectMapper;
    protected final String topic;

    public void publish(T event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic, message);
            log.info("Successfully published event: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event", e);
            throw new RuntimeException("Error serializing event", e);
        }
    }
}
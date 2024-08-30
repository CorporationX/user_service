package school.faang.user_service.service.publisher;

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
        String message = convertToMessage(event);
        sendMessage(message);
    }

    protected String convertToMessage(T event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event", e);
            throw new RuntimeException("Error serializing event", e);
        }
    }

    protected void sendMessage(String message) {
        redisTemplate.convertAndSend(topic, message);
    }
}

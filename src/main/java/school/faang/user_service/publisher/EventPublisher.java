package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class EventPublisher<T> {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    protected abstract String getChannel();

    public void publishMessage(T event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(getChannel(), jsonEvent);
            log.info("Published {}: {}", event.getClass().getName(), jsonEvent);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for {}", event.getClass().getName(), e);
        }
    }
}

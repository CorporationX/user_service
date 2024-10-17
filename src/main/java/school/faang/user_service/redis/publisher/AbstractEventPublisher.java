package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements EventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(T event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(getTopic().getTopic(), jsonEvent);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Failed to convert event to json");
        } catch (Exception exception) {
            log.error("Failed to send event to Redis", exception);
        }
    }

    abstract public Topic getTopic();
}

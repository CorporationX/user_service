package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
@RequiredArgsConstructor
@Slf4j
public class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    protected void convertAndSend(T event, String topic) {
        try {
            redisTemplate.convertAndSend(topic, objectMapper.writeValueAsString(event));
            log.info("Event published to topic: " + topic);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

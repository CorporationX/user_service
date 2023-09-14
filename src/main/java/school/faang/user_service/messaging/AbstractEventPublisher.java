package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String chanelName;

    protected void publishInChanel(T event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(chanelName, json);
    }
}

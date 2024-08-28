package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class AbstractPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String channel;

    public void publish(T t) {
        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e + "could not serialize object");
        }
        redisTemplate.convertAndSend(channel, jsonMessage);
    }

}

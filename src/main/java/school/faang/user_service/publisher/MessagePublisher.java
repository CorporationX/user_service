package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class MessagePublisher<T> {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    protected void convertAndSend(String topic, T event) {
        try {
            String jsonObject = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractMessagePublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void convertAndSend(String topic, T event) {
        try {
            String jsonObject = objectMapper.writeValueAsString(topic);
            redisTemplate.convertAndSend(topic, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

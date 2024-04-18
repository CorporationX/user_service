package school.faang.user_service.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String channelName;

    public void publish(T eventType) {
        String json;
        try {
            json = objectMapper.writeValueAsString(eventType);
        } catch (JsonProcessingException e) {
            log.error("Error when serializing an object to a JSON string", e);
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(channelName, json);
    }
}
package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class EventPublisher<T> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    protected void convertAndSend(T eventDto, String topicName) {
        try {
            String json = objectMapper.writeValueAsString(eventDto);
            redisTemplate.convertAndSend(topicName, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
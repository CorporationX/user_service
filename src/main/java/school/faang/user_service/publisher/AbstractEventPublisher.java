package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
abstract class AbstractEventPublisher<T> {
    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    // через конструктор не вариант, или придется прописывать конструкторы в классах наследниках
    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected void publish(T event, String channelName) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelName, jsonEvent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
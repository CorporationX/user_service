package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class EventPublisher<T> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    protected void convertAndSend(T eventDto, String topicName) {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            String json = objectMapper.writeValueAsString(eventDto);
            redisTemplate.convertAndSend(topicName,json);
            log.info("Event was send to topic: {}", topicName);
        } catch (JsonProcessingException e) {
            log.info("Convert to JSON failed.");
            throw new RuntimeException(e.getMessage());
        }
    }
}

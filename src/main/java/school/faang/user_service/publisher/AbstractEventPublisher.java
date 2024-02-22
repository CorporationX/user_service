package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public abstract class AbstractEventPublisher<T> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    abstract void publish(T t);
    protected void convertAndSend(T event, String channelTopicName) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
            log.debug("converted event {} to json", event);
        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException with event {}", event);
            throw new RuntimeException("Cannot serialize event to json");
        }
        redisTemplate.convertAndSend(channelTopicName, json);
        log.debug("json with event {} sent to topic {}", event, channelTopicName);
    }
}
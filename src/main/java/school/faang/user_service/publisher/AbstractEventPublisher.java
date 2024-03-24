package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    protected void send(String channelTopicName, T event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopicName, json);
        } catch (JsonProcessingException e) {
            log.warn("JsonProcessingException with event {}", event);
            throw new RuntimeException("Cannot serialize event to json");
        }
        log.info("Event was published {}", event);
    }
}
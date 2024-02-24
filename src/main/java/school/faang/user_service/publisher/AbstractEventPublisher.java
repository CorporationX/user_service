package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    protected void send(String channelTopicName, T event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
            log.debug("converted event {} to json", event);
        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException with event {}", event);
            throw new RuntimeException("Cannot serialize event to json");
        }
        redisTemplate.convertAndSend(channelTopicName, json);

        log.info("Event was published {}", event);
    }
}
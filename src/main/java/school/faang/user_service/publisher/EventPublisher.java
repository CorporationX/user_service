package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.exception.DataValidationException;

@RequiredArgsConstructor
@Slf4j
public abstract class EventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String canalName;

    protected void publishToChannel(T event) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Can't use publish ",e.getMessage());
            throw new DataValidationException("Problem with json");
        }
        redisTemplate.convertAndSend(canalName, json);
    }
}

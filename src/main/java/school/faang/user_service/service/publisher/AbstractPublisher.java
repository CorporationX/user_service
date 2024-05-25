package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Slf4j
public class AbstractPublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper jsonMapper;
    private final String channel;

    public void publish(T object) {
        String json;

        try {
            json = jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn( "Exception with json processing at class"  + AbstractPublisher.class);
            throw new RuntimeException(e);
        }

        redisTemplate.convertAndSend(channel, json);
    }
}

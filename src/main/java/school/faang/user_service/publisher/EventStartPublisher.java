package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventStartDto;

@Component
@RequiredArgsConstructor
public class EventStartPublisher {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${spring.data.redis.channels.event_start_channel.name}")
    private String channel;
    private final ObjectMapper objectMapper;

    public void publishMessage(EventStartDto event) {
        String jsonEvent;
        try {
            jsonEvent = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(channel, jsonEvent);
    }
}

package school.faang.user_service.publisher.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartDto;

@Component
@RequiredArgsConstructor
public class EventStartPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.channels.event_channel.name}")
    private String channel;

    public void publish(EventStartDto eventStartDto) {
        String json;
        try {
            json = objectMapper.writeValueAsString(eventStartDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(channel, json);
    }
}

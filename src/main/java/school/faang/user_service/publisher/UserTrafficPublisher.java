package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.UserTrafficDto;

@Component
@RequiredArgsConstructor
public class UserTrafficPublisher {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${spring.data.redis.channels.user_traffic_channel.name}")
    private String channel;
    private final ObjectMapper objectMapper;

    public void publishMessage(UserTrafficDto userTrafficDto) {
        String jsonEvent;
        try {
            jsonEvent = objectMapper.writeValueAsString(userTrafficDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(channel, jsonEvent);
    }
}

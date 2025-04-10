package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publisher.ProfileViewEvent;
import school.faang.user_service.exception.EventSerializationException;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channel.profile-view}")
    private String channel;

    public void publish(ProfileViewEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, jsonEvent);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("An error occurred while serializing the event");
        }
    }
}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userprofile.ProfilePicEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfilePicEventPublisher{

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channel.profile-pic}")
    private String profilePicTopic;

    public void publish(ProfilePicEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(profilePicTopic, json);
            log.info("Published ProfilePicEvent: {} to \"{}\"", json, profilePicTopic);
        } catch (JsonProcessingException exception) {
            log.error("Failed to serialize ProfilePicEvent for user {}: {}", event.getUserId(), exception.getMessage());
            throw new SerializationFailedException("Event serialization failed", exception);
        }
    }
}

package school.faang.user_service.publisher.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.ProfileViewEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher {

    @Value("${spring.data.redis.channels.profile-view-channel.name}")
    private String profileViewTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(ProfileViewEvent profileViewEvent) {
        try {
            String json = objectMapper.writeValueAsString(profileViewEvent);
            log.info("Publishing profile view event: {}", json);
            redisTemplate.convertAndSend(profileViewTopic, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize profile view event: {}", profileViewEvent, e);
            throw new RuntimeException(e);
        }
    }

}

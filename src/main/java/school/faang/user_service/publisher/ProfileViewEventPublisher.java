package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfileViewEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileViewEventPublisher {

    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannelName;

    public void publish(ProfileViewEvent profileViewEvent) {
        try {
            String json = objectMapper.writeValueAsString(profileViewEvent);
            redisMessagePublisher.publish(profileViewChannelName, json);
            log.info("profile viewed notification was published. {}", json);
        } catch (JsonProcessingException e) {
            log.error("profile viewed notification failed. {}", e.toString());
        }
    }
}

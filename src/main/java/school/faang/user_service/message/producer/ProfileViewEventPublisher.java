package school.faang.user_service.message.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.message.event.ProfileViewEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProfileViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.profile-view-channel.name}")
    private String profileViewEventTopic;

    public void publish(ProfileViewEvent event) {
        log.info("Sending ProfileViewEvent: {} to topic: {}", event, profileViewEventTopic);
        redisTemplate.convertAndSend(profileViewEventTopic, event);
    }
}

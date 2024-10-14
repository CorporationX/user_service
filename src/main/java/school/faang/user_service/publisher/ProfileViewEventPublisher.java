package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEvent;

@RequiredArgsConstructor
@Component
public class ProfileViewEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.profile-view-channel.name}")
    private String topic;

    public void publish(ProfileViewEvent profileEvent) {
        redisTemplate.convertAndSend(topic, profileEvent);
    }
}

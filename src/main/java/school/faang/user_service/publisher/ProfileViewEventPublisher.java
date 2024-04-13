package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannel;

    @Override
    public void publish(ProfileViewEvent event) {
        redisTemplate.convertAndSend(profileViewChannel, event);
        log.info("Event published id:" + event.getViewingUserId());
    }
}

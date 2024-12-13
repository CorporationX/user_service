package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfilePicEvent;

@Component
@RequiredArgsConstructor
public class ProfilePicPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profilePicChannel;

    public void publish(ProfilePicEvent event) {
        redisTemplate.convertAndSend(profilePicChannel.getTopic(), event);
    }
}
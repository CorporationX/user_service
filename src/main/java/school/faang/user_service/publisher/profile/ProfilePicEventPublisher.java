package school.faang.user_service.publisher.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.profile.ProfilePicEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Component
@RequiredArgsConstructor
public class ProfilePicEventPublisher implements MessagePublisher<ProfilePicEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profilePicTopic;

    @Override
    public void publish(ProfilePicEvent message) {
        redisTemplate.convertAndSend(profilePicTopic.getTopic(), message);
    }
}

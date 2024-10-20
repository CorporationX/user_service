package school.faang.user_service.publisher;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.ProfileViewEventDto;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisherImpl implements ProfileViewEventPublisher{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profileViewTopic;
    @Override
    public void publish(ProfileViewEventDto profileEvent) {
        redisTemplate.convertAndSend(profileViewTopic.getTopic(), profileEvent);
    }
}

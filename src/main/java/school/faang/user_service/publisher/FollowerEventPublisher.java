package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.follower.FollowerEventDto;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    private final ChannelTopic followerTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(FollowerEventDto event) {
        redisTemplate.convertAndSend(followerTopic.getTopic(), event);
    }
}

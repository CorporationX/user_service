package school.faang.user_service.messaging.follow;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.FollowerEvent;
import school.faang.user_service.util.Mapper;

@Component
@RequiredArgsConstructor
public class FollowPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;
    private final Mapper mapper;

    public void publish(FollowerEvent event) {
        String eventJson = mapper.toJson(event);
        redisTemplate.convertAndSend(followerTopic.getTopic(), eventJson);
    }
}

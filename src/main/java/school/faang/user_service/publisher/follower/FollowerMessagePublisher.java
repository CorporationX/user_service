package school.faang.user_service.publisher.follower;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.follower.FollowerEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Service
@RequiredArgsConstructor
public class FollowerMessagePublisher implements MessagePublisher<FollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;

    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(followerTopic.getTopic(), event);
    }
}

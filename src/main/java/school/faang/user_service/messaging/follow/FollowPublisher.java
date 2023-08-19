package school.faang.user_service.messaging.follow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.FollowerEvent;
import school.faang.user_service.messaging.Publisher;

@Component
public class FollowPublisher extends Publisher<FollowerEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;

    public FollowPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate, ChannelTopic followerTopic) {
        super(objectMapper);
        this.redisTemplate = redisTemplate;
        this.followerTopic = followerTopic;
    }

    @Override
    public void publish(FollowerEvent event) {
        String eventJson = toJson(event);
        redisTemplate.convertAndSend(followerTopic.getTopic(), eventJson);
    }
}

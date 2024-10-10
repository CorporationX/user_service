package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.follower.FollowerEventDto;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEventDto> {

    private final ChannelTopic followerTopic;

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ChannelTopic topic) {
        super(redisTemplate);
        this.followerTopic = topic;
    }

    public void sendEvent(FollowerEventDto event) {
        publish(followerTopic, event);
    }
}

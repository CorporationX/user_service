package school.faang.user_service.publisher.follower;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.follower.FollowerEvent;
import school.faang.user_service.publisher.AbstractMessagePublisher;

@Service
public class FollowerMessagePublisher extends AbstractMessagePublisher<FollowerEvent> {

    private final ChannelTopic followerTopic;

    public FollowerMessagePublisher(RedisTemplate<String, FollowerEvent> followerEventRedisTemplate,
                                    ObjectMapper objectMapper,
                                    ChannelTopic followerTopic) {
        super(followerEventRedisTemplate, objectMapper);
        this.followerTopic = followerTopic;
    }

    @Override
    public ChannelTopic getTopic() {
        return followerTopic;
    }
}

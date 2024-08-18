package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.FollowerEvent;

@Service
public class FollowerMessagePublisher extends GenericMessagePublisher<FollowerEvent> {

    private final ChannelTopic followerTopic;

    public FollowerMessagePublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper, ChannelTopic followerTopic) {
        super(redisTemplate, objectMapper);
        this.followerTopic = followerTopic;
    }

    @Override
    public ChannelTopic getTopic() {
        return followerTopic;
    }
}
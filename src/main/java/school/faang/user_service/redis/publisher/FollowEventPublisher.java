package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.FollowerEvent;

@Component
public class FollowEventPublisher extends AbstractEventPublisher<FollowerEvent> {
    @Value("${spring.data.redis.channel-topics.follower-event.name}")
    private String userFollowTopicName;

    public FollowEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(userFollowTopicName);
    }
}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEvent;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEvent>{

    private final ChannelTopic followerTopic;

    @Autowired
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Qualifier("followerChannel") ChannelTopic followerTopic) {

        super(redisTemplate, objectMapper);
        this.followerTopic = followerTopic;
    }

    public void sendEvent(FollowerEvent follower) {
        publish(followerTopic, follower);
    }
}

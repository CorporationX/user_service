package school.faang.user_service.publisher.followerevent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.follower.FollowerEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Slf4j
@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEvent> {

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ChannelTopic followerTopic,
                                  ObjectMapper objectMapper) {
        super(redisTemplate, followerTopic, objectMapper);
    }
}

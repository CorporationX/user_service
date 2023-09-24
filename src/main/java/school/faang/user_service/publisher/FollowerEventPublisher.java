package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.FollowerEvent;

import java.time.LocalDateTime;

@Slf4j
@Component
public class FollowerEventPublisher extends AbstractEventPublisher {
    @Setter
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerEventChannelName;

    @Autowired
    public FollowerEventPublisher(EventMapper eventMapper, ObjectMapper objectMapper,
                                  RedisMessagePublisher redisMessagePublisher) {
        super(eventMapper, objectMapper, redisMessagePublisher);
    }

    public void followerSubscribed(Long followerId, Long followeeId) {
        FollowerEvent followerEvent = new FollowerEvent();

        followerEvent.setEventType(EventType.FOLLOWER);
        followerEvent.setReceivedAt(LocalDateTime.now());
        followerEvent.setFollowerId(followerId);
        followerEvent.setFolloweeId(followeeId);

        redisMessagePublisher.publish(followerEventChannelName, followerEvent);
        log.info("Follower subscription notification was successfully published");
    }
}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.FollowerEvent;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {
    @Setter
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerEventChannelName;

    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;

    public void followerSubscribed(Long followerId, Long followeeId) {
        FollowerEvent followerEvent = new FollowerEvent();

        followerEvent.setEventType(EventType.FOLLOWER);
        followerEvent.setReceivedAt(new Date());
        followerEvent.setFollowerId(followerId);
        followerEvent.setFolloweeId(followeeId);

        try {
            String json = objectMapper.writeValueAsString(followerEvent);

            redisMessagePublisher.publish(followerEventChannelName, json);
            log.info("Follower subscription notification was successfully published");
        } catch (JsonProcessingException e) {
            log.error("Failed to convert FollowerEvent to JSON: {}", e.getMessage());
        }
    }
}

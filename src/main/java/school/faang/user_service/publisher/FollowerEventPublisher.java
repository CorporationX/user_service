package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.follower.FollowerEvent;
import school.faang.user_service.model.EventType;



@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {
    @Setter
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerEventChannelName;

    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;

    public void followerSubscribed(Long followerId, Long followeeId) {
        FollowerEvent followerEvent = new FollowerEvent();

        followerEvent.setEventType(EventType.FOLLOWER);
        followerEvent.setReceivedAt(LocalDateTime.now());
        followerEvent.setFollowerId(followerId);
        followerEvent.setFolloweeId(followeeId);

        try {
            String json = objectMapper.writeValueAsString(followerEvent);

            redisMessagePublisher.publish(followerEventChannelName, json);
        } catch (JsonProcessingException e) {
        }
    }
}

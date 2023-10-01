package school.faang.user_service.publisher;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEventDto;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper mapper;

    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerTopicName;

    public void sendEvent(FollowerEventDto event) {
        log.info("User subscription event sending with followerId: {}, followeeid: {}, started",
                event.getFollowerId(), event.getFolloweeId());

        String json = mapper.writeValueAsString(event);
        redisTemplate.convertAndSend(followerTopicName, json);
    }
}
package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.UserFollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements EventPublisher<UserFollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.follower}")
    private String followerEventChannel;

    @Override
    public void publish(UserFollowerEvent event) {
        redisTemplate.convertAndSend(followerEventChannel, event);
        log.info("Follower event published: {}", event);
    }
}

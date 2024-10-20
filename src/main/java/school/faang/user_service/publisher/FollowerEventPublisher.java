package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements EventPublisher<FollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(FollowerEvent event) {
        String channelName = redisProperties.getChannels().get("follower-event-channel");
        redisTemplate.convertAndSend(channelName, event);
    }
}

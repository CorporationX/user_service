package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent> {

    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String channelTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(FollowerEvent message) {
        try {
            redisTemplate.convertAndSend(channelTopic, message);
        } catch (Exception e) {
            log.error("Error sending follower event", e);
            throw new RuntimeException("Error sending follower event", e);
        }
    }
}

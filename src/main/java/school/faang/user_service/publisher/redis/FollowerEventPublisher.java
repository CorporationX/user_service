package school.faang.user_service.publisher.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {
    private final RedisTemplate<String, Object> redisPublisherTemplate;

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String topic;

    public void publish(FollowerEvent event) {
        redisPublisherTemplate.convertAndSend(topic, event);
    }
}

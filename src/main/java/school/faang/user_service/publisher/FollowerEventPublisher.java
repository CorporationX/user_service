package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;

    @Override
    public void publish(FollowerEvent followerEvent) {
        log.info("Publishing message on Redis channel {} with content: {}", followerTopic.getTopic(), followerEvent);
        redisTemplate.convertAndSend(followerTopic.getTopic(), followerEvent);
    }
}

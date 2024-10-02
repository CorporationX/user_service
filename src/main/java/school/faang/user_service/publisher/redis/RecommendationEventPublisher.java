package school.faang.user_service.publisher.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationChannel;

    @Override
    public void publishMessage(String message) {

        log.info("Sending to Redis: {}", message);
        redisTemplate.convertAndSend(recommendationChannel.getTopic(), message);
    }
}

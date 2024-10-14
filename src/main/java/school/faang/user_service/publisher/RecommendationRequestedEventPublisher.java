package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class RecommendationRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationRequestTopic;

    public void publish(String message) {
        redisTemplate.convertAndSend(recommendationRequestTopic.getTopic(), message);
        log.info("publisher is publish");
    }

}

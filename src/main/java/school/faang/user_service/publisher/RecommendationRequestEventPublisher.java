package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.RecommendationRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestEventPublisher implements MessagePublisher<RecommendationRequestEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(RecommendationRequestEvent event) {
        Long count = redisTemplate.convertAndSend(redisProperties.getChannel().getRecommendationRequestChannel(), event);
        log.info("Published RecommendationRequestEvent: {} to channel {}. Receivers count: {}",
                event, redisProperties.getChannel().getRecommendationRequestChannel(), count);
    }
}

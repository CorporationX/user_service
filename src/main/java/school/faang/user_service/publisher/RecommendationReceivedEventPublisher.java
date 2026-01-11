package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {
    @Value("${spring.data.redis.channel.recommendation}")
    private String recommendationTopicName;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(RecommendationReceivedEvent event) {
        redisTemplate.convertAndSend(recommendationTopicName, event);
    }
}

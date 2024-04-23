package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher implements MessagePublisher<RecommendationEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationTopic;


    public void publish(RecommendationEvent event) {
        redisTemplate.convertAndSend(recommendationTopic.getTopic(), event);
    }
}

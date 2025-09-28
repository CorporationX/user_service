package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.RecommendationRequestedEvent;

@Service
@RequiredArgsConstructor
public class RedisRecommendationRequestedEventPublisher implements RecommendationRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationTopic;

    @Override
    public void publish(RecommendationRequestedEvent event) {
        redisTemplate.convertAndSend(recommendationTopic.getTopic(), event);
    }
}
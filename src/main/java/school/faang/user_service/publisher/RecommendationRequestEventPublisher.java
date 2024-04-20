package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestEvent;
@Component
@RequiredArgsConstructor
public class RecommendationRequestEventPublisher implements MessagePublisher<RecommendationRequestEvent>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationRequestTopic;

    @Override
    public void publish(RecommendationRequestEvent event) {
        redisTemplate.convertAndSend(recommendationRequestTopic.getTopic(),event);
    }
}

package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.RecommendationRequestedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationRequestTopic;

    public void publish(RecommendationRequestedEvent recommendationRequestedEvent) {
        redisTemplate.convertAndSend(recommendationRequestTopic.getTopic(), recommendationRequestedEvent);
    }
}

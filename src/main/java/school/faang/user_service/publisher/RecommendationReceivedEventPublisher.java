package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationReceivedTopic;

    public void publish(RecommendationReceivedEvent event) {
        redisTemplate.convertAndSend(recommendationReceivedTopic.getTopic(), event);
    }
}

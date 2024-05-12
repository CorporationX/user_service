package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;

@Component
public class RecommendationEventPublisher extends AbstractMessagePublisher<RecommendationEvent> {
    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }
    @Autowired
    private ChannelTopic recommendationTopic;

    public void publish(RecommendationEvent event) {
        convertAndSend(recommendationTopic.getTopic(), event);
    }
}

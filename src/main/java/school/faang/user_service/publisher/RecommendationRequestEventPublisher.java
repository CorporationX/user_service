package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestEvent;

@Component
public class RecommendationRequestEventPublisher extends AbstractMessagePublisher<RecommendationRequestEvent> {
    public RecommendationRequestEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }
    @Autowired
    private ChannelTopic recommendationRequestTopic;

    @Override
    public void publish(RecommendationRequestEvent event) {
        convertAndSend(recommendationRequestTopic.getTopic(), event);
    }
}

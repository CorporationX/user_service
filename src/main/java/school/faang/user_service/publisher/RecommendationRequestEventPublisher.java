package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestEvent;

@Component
public class RecommendationRequestEventPublisher extends AbstractEventPublisher<RecommendationRequestEvent> {
    public RecommendationRequestEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    private ChannelTopic recommendationRequestTopic;

    public void publish(RecommendationRequestEvent event) {
        convertAndSend(recommendationRequestTopic.getTopic(), event);
    }
}

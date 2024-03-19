package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestEvent;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationRequestEvent> {
    @Value("${spring.data.redis.channels.recommendation_requested_channel.name}")
    private String recommendationRequestedChannelName;

    public RecommendationRequestedEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    public void publish(RecommendationRequestEvent recommendationRequestedEvent) {
        convertAndSend(recommendationRequestedEvent, recommendationRequestedChannelName);
    }
}

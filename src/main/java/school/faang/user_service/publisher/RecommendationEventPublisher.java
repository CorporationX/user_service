package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationEvent;

@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<RecommendationEvent>{
    @Value("${spring.data.redis.channels.recommendation_channel.name}")
    private String recommendationChannel;

    public RecommendationEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    public void publish(RecommendationEvent recommendationEvent) {
        convertAndSend(recommendationEvent, recommendationChannel);
    }
}

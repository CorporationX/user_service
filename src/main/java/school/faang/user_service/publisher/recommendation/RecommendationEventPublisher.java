package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;


@Component
@Slf4j
public class RecommendationEventPublisher extends EventPublisherAbstract<RecommendationEvent> {

    @Value("${spring.data.redis.channels.recommendation-event-channel}")
    private String topicRecommendationEvent;

    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(RecommendationEvent event) {
        handleEvent(event, topicRecommendationEvent);
    }
}

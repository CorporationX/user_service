package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventRecommendationRequestDto;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<EventRecommendationRequestDto> {

    @Autowired
    public RecommendationRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                                 @Value("${spring.data.redis.channels.recommendation_requested_event_channel}") String topicRecommendationRequestedEvent) {
        super(redisTemplate, objectMapper, topicRecommendationRequestedEvent);
    }

    public void publish(EventRecommendationRequestDto event) {
        publishInTopic(event);
    }
}

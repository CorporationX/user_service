package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.RecommendationEventDto;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationEventDto> {
    @Value("${spring.data.redis.channels.recommendationChannel}")
    private String recommendationChannelName;

    public RecommendationRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }


    @Override
    public void publish(RecommendationEventDto event) {
        convertAndSend(recommendationChannelName, event);
    }
}

package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.RecommendationEvent;

@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<RecommendationEvent> {
    public RecommendationEventPublisher(
            RedisTemplate<String, Object> template,
            ObjectMapper mapper,
            @Qualifier("recommendationChannel") String topic
    ) {
        super(template, mapper, topic);
    }
}

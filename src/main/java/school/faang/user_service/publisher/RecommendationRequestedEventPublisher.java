package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.RecommendationEventDto;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationEventDto> {
    public RecommendationRequestedEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Qualifier("recommendationTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}

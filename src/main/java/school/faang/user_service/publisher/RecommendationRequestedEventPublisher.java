package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.RecommendationRequestedEvent;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationRequestedEvent> {
    public RecommendationRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                 ObjectMapper objectMapper,
                                                 @Qualifier("recommendationRequestedTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
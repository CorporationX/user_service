package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventRecommendationRequestDto;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<EventRecommendationRequestDto> {

    private final ChannelTopic topicRecommendationRequestedEvent;

    @Autowired
    public RecommendationRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topicRecommendationRequestedEvent) {
        super(redisTemplate, objectMapper);
        this.topicRecommendationRequestedEvent = topicRecommendationRequestedEvent;
    }

    public void publish(EventRecommendationRequestDto event) {
        publishInTopic(topicRecommendationRequestedEvent, event);
    }
}

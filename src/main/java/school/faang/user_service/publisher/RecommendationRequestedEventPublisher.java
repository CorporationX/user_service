package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventRecommendationRequestDto;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<EventRecommendationRequestDto> {

    private final ChannelTopic topicRecommendationRequest;

    @Autowired
    public RecommendationRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topicRecommendationRequested, ChannelTopic topicRecommendationRequest) {
        super(redisTemplate, objectMapper);
        this.topicRecommendationRequest = topicRecommendationRequest;
    }

    public void publish(EventRecommendationRequestDto event) {
        publishInTopic(topicRecommendationRequest, event);
    }
}

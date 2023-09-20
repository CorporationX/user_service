package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import java.time.LocalDateTime;

@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<EventRecommendationRequestDto> {

    private final RecommendationRequestMapper mapper;
    private final ChannelTopic topicRecommendationRequest;

    @Autowired
    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                        RecommendationRequestMapper mapper, ChannelTopic topicRecommendationRequest) {
        super(redisTemplate, objectMapper);
        this.mapper = mapper;
        this.topicRecommendationRequest = topicRecommendationRequest;
    }

    public void publish(RecommendationRequest recommendationRequest) {
        EventRecommendationRequestDto event = mapper.toEventDto(recommendationRequest);
        event.setReceivedAt(LocalDateTime.now());
        publishInTopic(topicRecommendationRequest, event);
    }
}

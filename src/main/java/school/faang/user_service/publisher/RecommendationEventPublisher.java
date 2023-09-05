package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;

import java.time.LocalDateTime;

@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<EventRecommendationDto> {

    private final RecommendationMapper mapper;
    private final ChannelTopic topicRecommendation;

    @Autowired
    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                        RecommendationMapper mapper, ChannelTopic topicRecommendation) {
        super(redisTemplate, objectMapper);
        this.mapper = mapper;
        this.topicRecommendation = topicRecommendation;
    }

    public void publish(Recommendation recommendation) {
        EventRecommendationDto event = mapper.toEventDto(recommendation);
        event.setReceivedAt(LocalDateTime.now());
        publishInTopic(topicRecommendation, event);
    }
}

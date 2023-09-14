package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;

import java.time.LocalDateTime;

@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<EventRecommendationDto> {

    private final RecommendationMapper mapper;

    @Autowired
    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                        RecommendationMapper mapper,
                                        @Value("${spring.data.redis.channels.recommendation_channel}")
                                            String channelTopicName) {
        super(redisTemplate, objectMapper,channelTopicName);
        this.mapper = mapper;
    }

    public void publish(Recommendation recommendation) {
        EventRecommendationDto event = mapper.toEventDto(recommendation);
        event.setReceivedAt(LocalDateTime.now());
        publishInTopic(event);
    }
}

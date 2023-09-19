package school.faang.user_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;

@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<RecommendationEventDto> {
    private final RecommendationMapper recommendationMapper;

    @Autowired
    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                        ObjectMapper objectMapper,
                                        @Value(value = "${spring.data.redis.channels.recommendation_chanel.name}") String chanelName,
                                        RecommendationMapper recommendationMapper) {
        super(redisTemplate, objectMapper, chanelName);
        this.recommendationMapper = recommendationMapper;
    }

    public void publish(Recommendation recommendation) {
        RecommendationEventDto eventDto = recommendationMapper.toEventDto(recommendation);
        publishInChanel(eventDto);
    }
}

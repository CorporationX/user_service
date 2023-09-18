package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.RecommendationEventDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;

@Component
public class RecommendationEvent extends AbstractEventPublisher<RecommendationEventDto> {
    private final RecommendationMapper recommendationMapper;

    public RecommendationEvent(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                               @Value("${spring.data.redis.channels.recommendation_channel}") String topicRecommendation, RecommendationMapper recommendationMapper) {
        super(redisTemplate, objectMapper, topicRecommendation);
        this.recommendationMapper = recommendationMapper;
    }

    public void publish(Recommendation recommendation) {
        RecommendationEventDto event = recommendationMapper.toRecommendationEventDto(recommendation);
        publishInTopic(event);
    }
}

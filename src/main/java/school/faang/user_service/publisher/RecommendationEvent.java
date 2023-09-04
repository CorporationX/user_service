package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.RecommendationEventDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;

@Component
public class RecommendationEvent extends AbstractEventPublisher<RecommendationEventDto> {
    private final ChannelTopic topicInvitation;
    private final RecommendationMapper recommendationMapper;

    public RecommendationEvent(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topicInvitation, RecommendationMapper recommendationMapper) {
        super(redisTemplate, objectMapper);
        this.topicInvitation = topicInvitation;
        this.recommendationMapper = recommendationMapper;
    }

    public void publish(Recommendation recommendation) {
        RecommendationEventDto event = recommendationMapper.toEventDto(recommendation);
        publishInTopic(topicInvitation, event);
    }
}

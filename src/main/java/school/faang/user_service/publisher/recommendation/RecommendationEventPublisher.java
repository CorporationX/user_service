package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;
import school.faang.user_service.publisher.AbstractPublisher;

@Component
public class RecommendationEventPublisher extends AbstractPublisher<RecommendationEventDto> {

    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                        ObjectMapper mapper,
                                        @Value("${spring.data.redis.channels.recommendation_channel.name}") String channel) {
        super(redisTemplate,mapper, channel);
    }
}

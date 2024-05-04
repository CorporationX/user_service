package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestEvent;

@Component
public class RecommendationRequestEventPublisher extends AbstractEventPublisher<RecommendationRequestEvent> {
    public RecommendationRequestEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }
    @Value("${spring.data.redis.channels.recommendation_request_channel.name")
    private String recommendationRequestTopic;

    public void publish(RecommendationRequestEvent event) {
        convertAndSend(recommendationRequestTopic, event);
    }
}

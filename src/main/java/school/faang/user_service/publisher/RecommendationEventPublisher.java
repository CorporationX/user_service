package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.RecommendationEvent;

@Component
public class RecommendationEventPublisher implements MessagePublisher<RecommendationEvent> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.redis.channels.recommendation_channel}")
    private ChannelTopic recommendationTopic;

    public RecommendationEventPublisher(
            RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publish(RecommendationEvent event) {
        redisTemplate.convertAndSend(recommendationTopic.getTopic(), event);
    }
}

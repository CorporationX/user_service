package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.RecommendationReceivedEvent;

@Component
public class RecommendationReceivedEventPublisher implements MessagePublisher<RecommendationReceivedEvent> {

    private final RedisTemplate<String, RecommendationReceivedEvent> redisTemplate;
    private final ChannelTopic topic;

    public RecommendationReceivedEventPublisher(RedisTemplate<String, RecommendationReceivedEvent> redisTemplate,
                                                @Qualifier("recommendationReceivedTopic") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(RecommendationReceivedEvent recommendationReceivedEvent) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), recommendationReceivedEvent);
        } catch (Exception e) {
            throw new RuntimeException("Not able to publish message to topic %s".formatted(topic.getTopic()), e);
        }
    }
}
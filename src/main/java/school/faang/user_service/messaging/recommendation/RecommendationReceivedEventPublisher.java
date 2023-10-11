package school.faang.user_service.messaging.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.util.JsonMapper;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {
    private final ChannelTopic recommendationReceivedChannel;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonMapper jsonMapper;

    public void publish(RecommendationReceivedEvent event) {
        jsonMapper.toJson(event)
                .ifPresent(json -> redisTemplate.convertAndSend(recommendationReceivedChannel.getTopic(), json));
    }
}

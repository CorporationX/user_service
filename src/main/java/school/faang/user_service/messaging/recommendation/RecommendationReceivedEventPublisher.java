package school.faang.user_service.messaging.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.util.JsonMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationReceivedEventPublisher {
    private final ChannelTopic recommendationReceivedChannel;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonMapper jsonMapper;

    public void publish(RecommendationReceivedEvent event) {
        jsonMapper.toJson(event)
                .ifPresent(json -> redisTemplate.convertAndSend(recommendationReceivedChannel.getTopic(), json));
        log.info(event.getClass() + " " + "send");
    }
}

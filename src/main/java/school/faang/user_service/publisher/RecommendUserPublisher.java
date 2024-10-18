package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.RecommendationEventDto;
import school.faang.user_service.event.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
public class RecommendUserPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationTopic;

    public void publish(RecommendationEventDto event) {
        System.out.println("before sending message");
        redisTemplate.convertAndSend(recommendationTopic.getTopic(), event);
        System.out.println("after sending message");
    }
}

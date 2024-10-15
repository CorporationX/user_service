package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic goalCompletedTopic;

    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend(goalCompletedTopic.getTopic(), event);
    }
}

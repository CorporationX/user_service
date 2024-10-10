package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher implements MessagePublisher<GoalCompletedEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic goalCompletedEventTopic;

    public void publish(GoalCompletedEvent message) {
        redisTemplate.convertAndSend(goalCompletedEventTopic.getTopic(), message);
    }
}
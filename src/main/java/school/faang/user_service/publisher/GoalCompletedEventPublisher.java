package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.GoalCompletedEventDto;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("{spring.data.redis.channels.goal-completed.name}")
    private String goalCompletedChannelName;

    public void publish(GoalCompletedEventDto event) {
        redisTemplate.convertAndSend(goalCompletedChannelName, event);
    }
}

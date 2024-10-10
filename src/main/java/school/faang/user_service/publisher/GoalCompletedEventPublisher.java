package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.event.GoalCompletedEventDto;

@Component
public class GoalCompletedEventPublisher extends AbstractEventPublisher<GoalCompletedEventDto> {

    @Value("${spring.data.redis.channels.goal-completed-event-channel.name}")
    private String goalCompletedEvent;

    public GoalCompletedEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public void publish(GoalCompletedEventDto event) {
        convertAndSend(event, goalCompletedEvent);
    }
}

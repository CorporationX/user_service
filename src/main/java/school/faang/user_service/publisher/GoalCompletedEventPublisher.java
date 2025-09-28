package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.EventPublishingException;

@Component
@Slf4j
public class GoalCompletedEventPublisher {

    private final RedisTemplate<String, Object> goalRedisTemplate;
    private final String goalCompletedChannel;

    public GoalCompletedEventPublisher(
            RedisTemplate<String, Object> goalRedisTemplate,
            @Value("${spring.data.redis.channel.goal-completed}") String goalCompletedChannel
    ) {
        this.goalRedisTemplate = goalRedisTemplate;
        this.goalCompletedChannel = goalCompletedChannel;
    }

    public void publish(GoalCompletedEvent goalCompletedEvent) {
        try {
            goalRedisTemplate.convertAndSend(goalCompletedChannel, goalCompletedEvent);
            log.info("Published GoalCompletedEvent: userId={}, goalId={}",
                    goalCompletedEvent.userId(), goalCompletedEvent.goalId());
        } catch (Exception e) {
            log.error("Failed to publish GoalCompletedEvent: userId={}, goalId={}",
                    goalCompletedEvent.userId(), goalCompletedEvent.goalId(), e);
            throw new EventPublishingException("Failed to publish GoalCompletedEvent: userId="
                    + goalCompletedEvent.userId() + ", goalId=" + goalCompletedEvent.goalId(), e);
        }
    }
}

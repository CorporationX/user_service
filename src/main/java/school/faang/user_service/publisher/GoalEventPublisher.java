package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.GoalSetEvent;

@Component
@RequiredArgsConstructor
public class GoalEventPublisher {
    @Value("${spring.data.redis.channels.goal_channel.name}")
    private String goalSetChannelName;
    private final RedisMessagePublisher redisMessagePublisher;
    private final UserContext userContext;

    public void publishSetGoalEvent(Long goalId) {
        Long userId = userContext.getUserId();
        GoalSetEvent goalSetEvent = new GoalSetEvent(userId, goalId);
        redisMessagePublisher.publish(goalSetChannelName, goalSetEvent);
    }
}

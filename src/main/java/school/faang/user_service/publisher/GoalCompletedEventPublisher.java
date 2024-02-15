package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.dto.goal.GoalSetEvent;

@Component
public class GoalCompletedEventPublisher extends AbstractEventPublisher<GoalCompletedEvent> {
    @Value("${spring.data.redis.channels.goal_completed_channel.name}")
    private String goalCompletedChannelName;

    public void publish(GoalCompletedEvent event) {
        super.publish(event, goalCompletedChannelName);
    }
}

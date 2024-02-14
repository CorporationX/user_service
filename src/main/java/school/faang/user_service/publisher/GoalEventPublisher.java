package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalSetEvent;

@Component
public class GoalEventPublisher extends AbstractEventPublisher<GoalSetEvent> {
    @Value("${spring.data.redis.channels.goal_channel.name}")
    private String goalChannelName;

    public void publish(GoalSetEvent event) {
        super.publish(event, goalChannelName);
    }
}

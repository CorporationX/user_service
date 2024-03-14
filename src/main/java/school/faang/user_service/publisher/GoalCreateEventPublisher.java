package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalSetEvent;

@Component
public class GoalCreateEventPublisher extends AbstractEventPublisher<GoalSetEvent> {
    @Value("${spring.data.redis.channels.goal_created_channel.name}")
    private String goalChannelName;

    public void publish(GoalSetEvent event) {
        super.publish(event, goalChannelName);
    }
}

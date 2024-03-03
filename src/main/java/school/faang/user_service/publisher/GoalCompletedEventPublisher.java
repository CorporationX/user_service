package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalCompletedEvent;

@Component
@Slf4j
public class GoalCompletedEventPublisher extends AbstractEventPublisher<GoalCompletedEvent> {
    @Value("${spring.data.redis.channels.goal_completed_channel.name}")
    private String goalCompletedChannelName;
    public void publish(GoalCompletedEvent event) {
        publish(event, goalCompletedChannelName);
        log.info("Publishing event: {}", event);
    }
}

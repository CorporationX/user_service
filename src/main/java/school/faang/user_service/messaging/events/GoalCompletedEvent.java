package school.faang.user_service.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GoalCompletedEvent {
    private Long completedGoalId;
}

package school.faang.user_service.dto.goal;

import lombok.Data;

@Data
public class GoalCompletedEvent {
    private Long goalId;
    private Long userId;
}

package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletedEvent {
    private Long userId;
    private Long goalId;
}

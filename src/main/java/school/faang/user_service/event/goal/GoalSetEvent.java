package school.faang.user_service.event.goal;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GoalSetEvent {
    private Long userId;
    private Long goalId;
}

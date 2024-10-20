package school.faang.user_service.event.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalSetEvent {
    private Long userId;
    private Long goalId;
}

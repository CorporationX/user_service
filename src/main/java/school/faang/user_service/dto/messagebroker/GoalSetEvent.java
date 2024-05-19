package school.faang.user_service.dto.messagebroker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GoalSetEvent {
    Long userId;
    Long goalId;
}

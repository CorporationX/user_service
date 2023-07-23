package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@AllArgsConstructor
public class GoalFilterDto {
    private GoalStatus status;
}

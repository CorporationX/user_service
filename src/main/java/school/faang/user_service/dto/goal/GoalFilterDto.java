package school.faang.user_service.dto.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@NoArgsConstructor
public class GoalFilterDto {
    private String title;
    private GoalStatus status;
}

package school.faang.user_service.model.dto.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.entity.goal.GoalStatus;

@Data
@NoArgsConstructor
public class GoalFilterDto {
    private String title;
    private String description;
    private GoalStatus status;
}

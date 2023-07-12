package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalFilterDto {
    private String title;
    private GoalStatus goalStatus;
    private Long skillId;
}

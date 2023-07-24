package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@AllArgsConstructor
@Builder
public class GoalFilterDto {
    private GoalStatus status;
    private String title;
}

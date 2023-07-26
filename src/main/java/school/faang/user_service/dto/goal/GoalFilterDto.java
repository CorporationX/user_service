package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalFilterDto {
    private String title;
    private GoalStatus goalStatus;
    private Long skillId;
}

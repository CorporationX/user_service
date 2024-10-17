package school.faang.user_service.model.dto.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.enums.GoalStatus;

@Data
@NoArgsConstructor
public class GoalFilterDto {
    private String title;
    private String description;
    private GoalStatus status;
}

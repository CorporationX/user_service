package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private GoalStatus status;
    private Long skillId;
}

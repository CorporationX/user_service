package school.faang.user_service.filters.goal.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@Builder
public class GoalFilterDto {
    private String title;
    private String description;
    private GoalStatus status;
}
package school.faang.user_service.dto.goal.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalFilterDto {
    private String titleFilter;
    private GoalStatus statusFilter;
}
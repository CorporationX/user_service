package school.faang.user_service.dto.goal;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
public class GoalFilterDto {
    private GoalStatus status;
    private List<Long> skillIds;
}

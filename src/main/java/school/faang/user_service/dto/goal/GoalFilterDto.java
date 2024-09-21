package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Getter
@Setter
public class GoalFilterDto {
    private GoalStatus status;
    private List<Long> skillIds;
}

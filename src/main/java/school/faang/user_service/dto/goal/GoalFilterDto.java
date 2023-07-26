package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class GoalFilterDto {
    private String titlePattern;
    private List<Long> skillIds;
    private GoalStatus status;
    private Long parentId;
}

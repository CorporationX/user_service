package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@AllArgsConstructor
@Getter
public class GoalFilterDto {
    private String description;
    private Long parentId;
    private String title;
    private GoalStatus status;
    private List<Long> skillIds;
}

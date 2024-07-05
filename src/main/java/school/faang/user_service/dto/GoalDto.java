package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private Goal parent;
    private GoalStatus status;
    private List<Skill> skillsToAchieve;
}

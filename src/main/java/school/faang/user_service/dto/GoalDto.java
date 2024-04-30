package school.faang.user_service.dto;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

public class GoalDto {
    private Long id;
    private Goal parent;
    private String title;
    private String description;
    private GoalStatus status;
    private List<Long> userIds;
    private List<Long> skillIds;
}

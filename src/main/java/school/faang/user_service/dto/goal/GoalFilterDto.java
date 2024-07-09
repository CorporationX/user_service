package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class GoalFilterDto {
    private Long id;
    private String descriptionPattern;
    private Long parentId;
    private String titlePattern;
    private GoalStatus status;
    private List<Long> skillIds;
}

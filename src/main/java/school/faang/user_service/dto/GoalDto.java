package school.faang.user_service.dto;

import lombok.Data;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalMapper;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private GoalStatus status;
    private List<Long> skillsIds = new ArrayList<>();
    private List<Long> goalIdList = getSkillsIds();
}

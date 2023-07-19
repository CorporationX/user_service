package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGoalDto {
    private Long id;
    private String title;
    private GoalStatus status;
    private List<SkillDto> skillDtos;
    private List<Long> userIds;
}

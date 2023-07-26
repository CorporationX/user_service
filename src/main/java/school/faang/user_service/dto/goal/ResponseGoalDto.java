package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Data
public class ResponseGoalDto {
    private Long id;
    private Long parentId;
    private String title;
    private String description;
    private List<SkillDto> skillsToAchieve;
}

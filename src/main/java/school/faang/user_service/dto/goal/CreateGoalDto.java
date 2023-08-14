package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Data
@Builder
public class CreateGoalDto {
    @Min(value = 0, message = "ParentId should be a positive value")
    private Long parentId;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotNull(message = "Skills To Achieve cannot be null")
    private List<SkillDto> skillsToAchieve;
}

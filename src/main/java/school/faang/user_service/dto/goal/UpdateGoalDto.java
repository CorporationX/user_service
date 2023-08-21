package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Min(value = 0, message = "ParentId should be a positive value")
    private Long id;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotNull(message = "Skills cannot be null")
    private GoalStatus status;
    @NotNull(message = "Skills cannot be null")
    private List<SkillDto> skillDtos;
    private List<Long> userIds;
}

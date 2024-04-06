package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    private Long id;
    private Long parentId;
    @NotBlank(message = "Goal must have a title")
    @Size(max = 150, message = "Goal title can't be longer than 150 characters")
    private String title;
    private GoalStatus status;
    @Size(max = 500, message = "Goal description can't be longer than 500 characters")
    private String description;
    @NotNull(message = "Goal must have at least one skill required")
    @Size(min = 1, message = "Goal must have at least one skill required")
    private List<Long> skillIds;
}
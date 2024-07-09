package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
public class GoalDto {

    private Long id;
    @NotNull(message = "description is required")
    @NotBlank
    private String description;
    private Long parentId;
    @NotNull(message = "title is required")
    @NotBlank
    private String title;
    private GoalStatus status;
    @NotNull
    private List<Long> skillIds;
}

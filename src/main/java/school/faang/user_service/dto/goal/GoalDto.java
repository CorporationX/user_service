package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private GoalStatus status;

    private List<Long> skillIds;
}

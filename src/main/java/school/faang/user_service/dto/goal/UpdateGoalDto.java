package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGoalDto {
    @NotNull(message = "Status cannot be null")
    private Long goalId;

    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 64, message = "Title cannot be longer than 64 characters")
    private String title;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 128, message = "Description cannot be longer than 128 characters")
    private String description;

    private List<Long> skillIds;

    @NotNull(message = "Status cannot be null")
    private GoalStatus status;
}

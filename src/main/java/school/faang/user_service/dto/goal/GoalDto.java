package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;


@Getter
@Setter
public class GoalDto {
    @Positive(message = "Id cannot be zero or negative")
    private Long id;
    @NotBlank(message = "Title cannot be empty or null")
    private String title;
    @NotBlank(message = "Title cannot be empty or null")
    private String description;
    private Long parent;
    @NotNull(message = "Status cannot be null")
    private GoalStatus status;
    private List<Long> skillIds;
}

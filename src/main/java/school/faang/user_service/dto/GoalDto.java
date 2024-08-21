package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
public class GoalDto {
    private Long id;

    @Size(max = 128, message = "The length of the description is longer than allowed")
    private String description;
    private long parentId;

    @NotEmpty(message = "Title cannot be empty")
    @Size(max = 64, message = "The length of the goal name is longer than allowed")
    private String title;

    @NotNull(message = "Status cannot be null")
    private GoalStatus status;

    private List<Long> skillIds;
}
package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record UpdateGoalDto(
        @NotBlank(message = "Title should be present")
        @Size(max = 255, message = "Title length cant be more than 255 character")
        String title,

        @NotBlank(message = "Description should be present")
        @Size(max = 1000, message = "Description length cant be more than 1000 character")
        String description,

        LocalDateTime deadline,
        Long mentorId,
        GoalStatus status
) {
}
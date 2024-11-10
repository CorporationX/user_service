package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UpdateGoalDto(

        @Positive
        Long parentId,

        @NotBlank(message = "Title cannot be blank")
        @Size(max = 64, message = "Title cannot be longer than 64 characters")
        String title,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 4096, message = "Description cannot be longer than 4096 characters")
        String description,

        @NotNull(message = "Status cannot be null")
        GoalStatusDto status,

        @NotNull(message = "Deadline cannot be null")
        @Future(message = "Deadline must be in the future")
        LocalDateTime deadline,

        @Positive
        Long mentorId,

        @NotNull(message = "Users list cannot be null")
        @Size(min = 1, message = "At least one user must be specified")
        List<Long> userIds,

        @NotNull(message = "Skills list cannot be null")
        @NotEmpty(message = "At least one skill must be specified")
        List<Long> skillsToAchieveIds
) {
}

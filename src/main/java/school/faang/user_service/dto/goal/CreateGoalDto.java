package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateGoalDto(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        @Future
        LocalDateTime deadline,
        Long mentorId,
        List<Long> userIds,
        List<Long> skillIds
) {
}

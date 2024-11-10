package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record GoalFilterDto(
        @Size(max = 64, message = "Title cannot be longer than 64 characters")
        String title,

        @Size(max = 4096, message = "Description cannot be longer than 4096 characters")
        String description,

        GoalStatusDto status,
        LocalDateTime deadline,
        List<Long> skillIds,
        List<Long> userIds
) {
}

package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import school.faang.user_service.entity.goal.GoalStatus;

public record GoalDto(
        Long id,
        Long parentId,
        @NotEmpty(message = "Название цели не может быть пустым")
        String title,
        @NotEmpty(message = "Описание цели не может быть пустым")
        String description,
        GoalStatus status,
        LocalDateTime deadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long mentorId,
        @NotNull(message = "Навыки не могут быть пустыми")
        List<Long> skillIds,
        List<Long> userIds
) {}
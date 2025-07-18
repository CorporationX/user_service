package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateGoalDto(
        @Schema(description = "Заголовок цели")
        @Nullable
        String title,

        @Nullable
        String description,

        @Schema(description = "Описание цели")
        @Nullable
        LocalDateTime deadline,

        @Schema(description = "Id ментора цели")
        @Nullable
        Long mentorId,

        @Schema(description = "Статус цели")
        @Nullable
        GoalStatus status,

        @Schema(description = "Список id навыков, которые прокачиваются целью")
        @Nullable
        List<Long> skillIds
) {
}

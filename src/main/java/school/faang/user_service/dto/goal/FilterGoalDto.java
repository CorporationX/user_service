package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import school.faang.user_service.entity.goal.GoalStatus;

public record FilterGoalDto(
        @Schema(description = "Заголовок цели содержит строку")
        @Nullable
        String titleContains,

        @Nullable
        @Schema(description = "Описание цели содержит строку")
        String descriptionContains,

        @Schema(description = "Цели с заданным статусом")
        @Nullable
        GoalStatus status,

        @Schema(description = "Цели с заданным ментором")
        @Nullable
        Long mentorId
) {
}

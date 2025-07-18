package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record GoalDto(
        @Schema(description = "Id цели")
        Long id,

        @Schema(description = "Заголовок цели")
        String title,

        @Schema(description = "Описание цели")
        String description,

        @Schema(description = "Статус цели")
        GoalStatus status,

        @Schema(description = "Дедлайн цели")
        LocalDateTime deadline,

        @Schema(description = "Дата создания цели")
        LocalDateTime createdAt,

        @Schema(description = "Дата изменения цели")
        LocalDateTime updatedAt
) {
}

package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreateGoalDto(
        @Schema(description = "Заголовок цели")
        @Size(max = 255, message = "Заголовок не должен превышать 255 символов")
        @NotBlank(message = "Введите заголовок цели")
        String title,

        @Schema(description = "Описание цели")
        @NotBlank(message = "Введите описание цели")
        String description,

        @Schema(description = "Дедлайн цели")
        @Future(message = "Некорректная дата дедлайна")
        @NotNull(message = "Введите дедлайн задачи")
        LocalDateTime deadline,

        @Schema(description = "Ментор цели")
        @Nullable
        Long mentorId,

        @Schema(description = "Участники цели")
        @NotEmpty(message = "Не заданы пользователи цели")
        List<Long> userIds,

        @Nullable
        @Schema(description = "Навыки, которые прокачиваются данной целью")
        List<Long> skillIds
) {
}

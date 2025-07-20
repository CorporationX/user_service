package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.ArraySchema;
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
        @Size(max = 255, message = "Заголовок не должен превышать 255 символов")
        @NotBlank(message = "Введите заголовок цели")
        @Schema(description = "Заголовок цели")
        String title,

        @NotBlank(message = "Введите описание цели")
        @Schema(description = "Описание цели")
        String description,

        @Future(message = "Некорректная дата дедлайна")
        @NotNull(message = "Введите дедлайн задачи")
        @Schema(description = "Дедлайн цели")
        LocalDateTime deadline,

        @Nullable
        @Schema(description = "Ментор цели")
        Long mentorId,

        @NotEmpty(message = "Не заданы пользователи цели")
        @Schema(description = "Участники цели. Ограничение на количество активных целей у участника"
                              + " описано в документации")
        List<Long> userIds,

        @Nullable
        @ArraySchema(
                schema = @Schema(type = "integer", format = "int64"),
                arraySchema = @Schema(description = "Навыки, которые прокачиваются данной целью")
        )
        List<Long> skillIds
) {
}

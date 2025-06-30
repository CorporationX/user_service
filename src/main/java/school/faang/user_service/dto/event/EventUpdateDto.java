package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

public record EventUpdateDto(
        @NotNull(message = "ID события обязательно")
        @Positive(message = "ID должен быть положительным числом")
        Long id,

        @NotBlank(message = "Название события обязательно")
        String title,

        @NotNull(message = "Дата начала обязательна")
        LocalDateTime startDate,

        @Future(message = "Дата окончания должна быть в будущем")
        LocalDateTime endDate,

        @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
        String description,

        List<@Positive Long> relatedSkills,

        @NotBlank(message = "Локация обязательна")
        String location,

        @Min(value = 1, message = "Минимальное количество участников - 1")
        int maxAttendees,

        @NotNull(message = "Тип события обязателен")
        EventType eventType,

        @NotNull(message = "Статус события обязателен")
        EventStatus eventStatus
) {}

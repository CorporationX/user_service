package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventDto(
        @NotBlank @Size(max = 64) String title,
        @NotBlank @Size(max = 4096) String description,
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate,
        @NotNull EventType type,
        @Size(max = 128) String location,
        @PositiveOrZero Integer maxAttendees,
        @NotNull @Size(max = 50) List<Long> relatedSkillIds
) {

}


package school.faang.user_service.dto.workschedule;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record WorkScheduleDto(
        @Min(1) long id,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull LocalTime startLunch,
        @NotNull LocalTime endLunch,
        @NotBlank String timezone
) {
}

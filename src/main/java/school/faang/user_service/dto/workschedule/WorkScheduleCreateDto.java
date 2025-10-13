package school.faang.user_service.dto.workschedule;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record WorkScheduleCreateDto(
        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @NotNull
        LocalTime startLunch,

        @NotNull
        LocalTime endLunch,

        @NotNull
        String timezone) {
}

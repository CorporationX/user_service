package school.faang.user_service.dto.workschedule;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record UpdateWorkScheduleDto(
        @NotNull
        LocalTime startTime,
        @NotNull
        LocalTime endTime,
        @NotNull
        LocalTime startLunch,
        @NotNull
        LocalTime endLunch,
        @NotNull
        String timezone
) {

}

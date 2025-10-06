package school.faang.user_service.dto.workschedule;

import java.time.LocalTime;

public record UpdateWorkScheduleDto(
        LocalTime startTime,
        LocalTime endTIme,
        LocalTime startLunch,
        LocalTime endLunch,
        String timezone
) {
}

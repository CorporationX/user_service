package school.faang.user_service.dto.workschedule;

import java.time.LocalTime;

public record WorkScheduleDto(
        Long id,
        LocalTime startTime,
        LocalTime endTime,
        LocalTime startLunch,
        LocalTime endLunch,
        String timezone // часовой пояс в формате IANA (например, "Europe/Moscow")
) {
}

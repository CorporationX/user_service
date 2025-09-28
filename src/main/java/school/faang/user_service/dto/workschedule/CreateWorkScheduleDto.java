package school.faang.user_service.dto.workschedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class CreateWorkScheduleDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime startLunch;
    private LocalTime endLunch;
    private String timezone;
}

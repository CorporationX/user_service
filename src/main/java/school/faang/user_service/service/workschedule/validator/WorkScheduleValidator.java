package school.faang.user_service.service.workschedule.validator;

import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalTime;

public class WorkScheduleValidator {
    public static void validateEntity(WorkSchedule workSchedule) {
        validateTimeSequence(
                workSchedule.getStartTime(),
                workSchedule.getStartLunch(),
                workSchedule.getEndLunch(),
                workSchedule.getEndTime()
        );
    }

    public static void validate(WorkScheduleDto dto) {
        validateTimeSequence(
                dto.startTime(),
                dto.startLunch(),
                dto.endLunch(),
                dto.endTime()
        );
    }

    public static void validateForCreate(WorkScheduleCreateDto dto) {
        validateTimeSequence(
                dto.startTime(),
                dto.startLunch(),
                dto.endLunch(),
                dto.endTime()
        );
    }

    private static void validateTimeSequence(LocalTime t1, LocalTime t2, LocalTime t3, LocalTime t4) {
        if (!t1.isBefore(t2)) {
            throw new DataValidationException("Start time must be before lunch start time.");
        }
        if (!t2.isBefore(t3)) {
            throw new DataValidationException("Lunch start time must be before lunch end time.");
        }
        if (!t3.isBefore(t4)) {
            throw new DataValidationException("Lunch end time must be before work end time.");
        }
    }
}

package school.faang.user_service.service.workschedule;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Component
public class WorkScheduleMapper {
    public WorkSchedule toEntity(WorkScheduleDto dto) {
        if (dto == null) {
            return null;
        }

        WorkSchedule workSchedule = new WorkSchedule();
        workSchedule.setId(dto.id());
        workSchedule.setStartTime(dto.startTime());
        workSchedule.setEndTime(dto.endTime());
        workSchedule.setStartLunch(dto.startLunch());
        workSchedule.setEndLunch(dto.endLunch());
        workSchedule.setTimezone(dto.timezone());

        return workSchedule;
    }

    public WorkScheduleDto toDto(WorkSchedule entity) {
        if (entity == null) {
            return null;
        }

        return new WorkScheduleDto(entity.getId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStartLunch(),
                entity.getEndLunch(),
                entity.getTimezone());
    }
}

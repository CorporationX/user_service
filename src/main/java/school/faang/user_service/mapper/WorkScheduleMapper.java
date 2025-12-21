package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    WorkSchedule toWorkSchedule(WorkScheduleDto workScheduleDto);

    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
}

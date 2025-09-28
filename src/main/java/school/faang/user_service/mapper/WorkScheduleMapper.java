package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface WorkScheduleMapper {
    WorkSchedule toEntity(WorkScheduleDto workScheduleDto);

    WorkScheduleDto toDto(WorkSchedule workSchedule);
}
package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {

    WorkSchedule toWorkSchedule(WorkScheduleDto workScheduleDto);

    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateWorkScheduleFromDto(WorkScheduleDto dto, @MappingTarget WorkSchedule workSchedule);
}

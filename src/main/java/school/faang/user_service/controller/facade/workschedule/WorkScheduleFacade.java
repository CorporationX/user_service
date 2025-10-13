package school.faang.user_service.controller.facade.workschedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkScheduleFacade {
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleDto addWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto) {
        WorkSchedule savedWorkSchedule = workScheduleService.addWorkSchedule(workScheduleCreateDto);

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(long workScheduleId, WorkScheduleUpdateDto workScheduleUpdateDto) {
        WorkSchedule workSchedule = workScheduleService.updateWorkSchedule(workScheduleId, workScheduleUpdateDto);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    public void deleteWorkSchedule(long workScheduleId) {
        workScheduleService.deleteWorkSchedule(workScheduleId);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleService.getById(workScheduleId);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }
}

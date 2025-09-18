package school.faang.user_service.service.workschedule;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;

@Service
public interface WorkScheduleService {
    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    WorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, WorkScheduleDto workScheduleDto);
    
    WorkScheduleDto getById(long workScheduleId);
}

package school.faang.user_service.controller.workschedule;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    public WorkScheduleDto addWorkSchedule(@NonNull WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(userContext.getUserId(), workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(@NotBlank long workScheduleId, @NonNull WorkScheduleDto workScheduleDto) {
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, workScheduleDto);
    }

    public WorkScheduleDto getById(@NotBlank long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}

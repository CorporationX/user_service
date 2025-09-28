package school.faang.user_service.controller.workschedule;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@RestController
@RequestMapping("/v1/schedule")
public class WorkScheduleController {
    private WorkScheduleService workScheduleService;
    private UserContext userContext;

    @GetMapping("/addworkschedule")
    public WorkScheduleDto addWorkSchedule(WorkScheduleDto dto) {
        return workScheduleService.addWorkSchedule(userContext.getUserId(), dto);
    }

    @GetMapping("/updateworkschedule")
    public WorkScheduleDto updateWorkSchedule(long workScheduleId, WorkScheduleDto dto) {
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, dto);
    }

    @GetMapping("/getbyid")
    public WorkScheduleDto getById(long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}

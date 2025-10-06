package school.faang.user_service.controller.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@RestController
@RequestMapping("/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    @PostMapping
    public WorkScheduleDto addWorkSchedule(@Valid @RequestBody WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(
                userContext.getUserId(),
                workScheduleDto
        );
    }

    @PutMapping("/{workScheduleId}")
    public WorkScheduleDto updateWorkSchedule(
            @PathVariable long workScheduleId,
            @Valid @RequestBody WorkScheduleDto workScheduleDto) {
        return workScheduleService.updateWorkSchedule(
                userContext.getUserId(),
                workScheduleId,
                workScheduleDto
        );
    }

    @GetMapping("/{workScheduleId}")
    WorkScheduleDto getById(@PathVariable long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}

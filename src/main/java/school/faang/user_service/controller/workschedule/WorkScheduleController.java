package school.faang.user_service.controller.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@RestController
@RequestMapping("/work-schedules")
@RequiredArgsConstructor
@Slf4j
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    @PostMapping
    public WorkScheduleDto addWorkSchedule(@Valid @RequestBody WorkScheduleDto workScheduleDto) {
        long userId = userContext.getUserId();
        validateSchedule(workScheduleDto, userId);
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    @PutMapping("/{workScheduleId}")
    public WorkScheduleDto updateWorkSchedule(
            @PathVariable long workScheduleId,
            @Valid @RequestBody WorkScheduleDto workScheduleDto) {
        long userId = userContext.getUserId();
        validateSchedule(workScheduleDto, userId);
        return workScheduleService.updateWorkSchedule(userId, workScheduleId, workScheduleDto);
    }

    @GetMapping("/{workScheduleId}")
    public WorkScheduleDto getById(@PathVariable long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }

    private void validateSchedule(WorkScheduleDto workScheduleDto, long userId) {
        if (!isValidSchedule(workScheduleDto)) {
            log.warn("Invalid work schedule times for user: {}", userId);
            throw new DataValidationException("Expected: start < startLunch < endLunch < end");
        }
    }

    private boolean isValidSchedule(WorkScheduleDto dto) {
        return dto.startTime().isBefore(dto.startLunch())
                && dto.startLunch().isBefore(dto.endLunch())
                && dto.endLunch().isBefore(dto.endTime());
    }
}

package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.goal.GoalInvitationService;

@Tag(name = "Управление приглашениями к цели")
@RestController
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @Operation(summary = "Добавить приглашение к цели")
    @PostMapping("/goal/invitation")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitationDto) {
        return goalInvitationService.createInvitation(invitationDto);
    }

    @Operation(summary = "Принять приглашение к цели")
    @PutMapping("/goal/invitation/{id}/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GoalInvitationDto acceptGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @Operation(summary = "Отклонить приглашение к цели")
    @PutMapping("/goal/invitation/{id}/reject")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GoalInvitationDto rejectGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }
}

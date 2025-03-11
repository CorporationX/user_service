package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;
@RestController
@RequestMapping("/goal/invitations")
@RequiredArgsConstructor
@Tag(name = "Goal Invitations", description = "API для управления приглашениями в цели")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    @Operation(summary = "Создать приглашение", description = "Создаёт новое приглашение для участия в цели")
    public void createInvitation(@Validated @RequestBody GoalInvitationDto invitation){
        goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/accept-invitation/{id}")
    @Operation(summary = "Принять приглашение", description = "Принмает приглашение для участия в цели")
    public void acceptGoalInvitation(
            @Parameter(description = "id приглашения к участию", required = true, example = "1")
            @PathVariable long id){
        goalInvitationService.acceptInvitation(id);
    }

    @PutMapping("/decline-invitation/{id}")
    @Operation(summary = "Отклонить приглашение", description = "Отклоняет приглашение для участия в цели")
    public void rejectGoalInvitation(
            @Parameter(description = "id приглашения к участию", required = true, example = "1")
            @PathVariable long id){
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("/get-goal-invitations")
    @Operation(summary = "Получить список приглашений", description = "Возвращает список приглашений по фильтру")
    public List<GoalInvitationDto> getInvitations(@Validated @RequestBody GoalInvitationFilterDto filter){
        return goalInvitationService.getInvitations(filter);
    }
}
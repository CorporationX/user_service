package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "GoalInvitation Controller", description = "Контроллер для приглашений на цель")
@RequestMapping("/goal-invitations")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @Operation(summary = "Создать приглашение", description = "Создать новое приглашение для цели")
    @ApiResponse(responseCode = "201", description = "Приглашение успешно создано")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @Operation(summary = "Принять приглашение", description = "Принять приглашение по идентификатору")
    @ApiResponse(responseCode = "200", description = "Приглашение успешно принято")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/accept")
    public void acceptGoalInvitation(@Min(1) @PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @Operation(summary = "Отклонить приглашение", description = "Отклонить приглашение по идентификатору")
    @ApiResponse(responseCode = "200", description = "Приглашение успешно отклонено")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/reject")
    public void rejectGoalInvitation(@Min(1) @PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @Operation(summary = "Получить приглашения", description = "Получить список всех приглашений на основе фильтра")
    @ApiResponse(responseCode = "200", description = "Список приглашений получен")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}

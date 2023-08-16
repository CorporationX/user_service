package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal/invitation")
@Tag(name = "Goal Invitation")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    @Operation(summary = "Отправить приглашение присоедениться к цели")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("accept/{id}")
    @Operation(summary = "Принять приглашение к цели")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable long id) {
        validateId(id);
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("reject/{id}")
    @Operation(summary = "Отклонить приглашение к цели")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable long id) {
        validateId(id);
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("/filters")
    @Operation(summary = "Просмотреть все приглашения с фильтрами")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }

    private void validateId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid request. Id can't be less than 0.");
        }
    }
}

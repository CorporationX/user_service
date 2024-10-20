package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.goal.GoalInvitationDto;
import school.faang.user_service.model.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.validator.groups.CreateGroup;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goal_invitations")
@RequiredArgsConstructor
@Validated
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@RequestBody @NotNull @Validated(CreateGroup.class) GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/{id}/accepting")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable @Positive long id) {
        return goalInvitationService.acceptInvitation(id);
    }

    @PutMapping("/{id}/rejection")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable @Positive long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(@NotNull InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
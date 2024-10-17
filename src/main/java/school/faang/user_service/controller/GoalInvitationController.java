package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.GoalInvitationDto;
import school.faang.user_service.model.filter_dto.InvitationFilterDto;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/goal-invitations")
public class GoalInvitationController {

    private final GoalInvitationService service;

    @PostMapping(value = "/create")
    public @Validated(GoalInvitationDto.AfterCreate.class) GoalInvitationDto createInvitation(
            @Validated(GoalInvitationDto.BeforeCreate.class) @RequestBody GoalInvitationDto goalInvitationDto) {
        return service.createInvitation(goalInvitationDto);
    }

    @PatchMapping(value = "/accept/{goalId}")
    public void acceptGoalInvitation(@PathVariable long goalId) {
        service.acceptGoalInvitation(goalId);
    }

    @PatchMapping(value = "/reject/{goalId}")
    public void rejectGoalInvitation(@PathVariable long goalId) {
        service.rejectGoalInvitation(goalId);
    }

    @GetMapping(value = "/invitations")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {

        if (filter == null) {
            throw new GoalInvitationValidationException("InvitationFilterDto must not be null");
        }

        if (filter.getInvitedNamePattern() == null
                && filter.getInviterNamePattern() == null
                && filter.getInviterId() == null
                && filter.getInvitedId() == null
                && filter.getStatus() == null

        ) {
            throw new GoalInvitationValidationException("At least one of field InvitationFilterDto must not be null");
        }

        return service.getInvitations(filter);
    }
}

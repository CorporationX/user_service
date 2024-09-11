package school.faang.user_service.controller.goal;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/goal-invitations")
public class GoalInvitationController {

    private final GoalInvitationService service;

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {

        if (goalInvitationDto.getInviterId() == null) {
            throw new GoalInvitationValidationException("InviterId must not be null");
        }

        if (goalInvitationDto.getInvitedUserId() == null) {
            throw new GoalInvitationValidationException("InvitedUserId must not be null");
        }

        if (goalInvitationDto.getGoalId() == null) {
            throw new GoalInvitationValidationException("GoalId must not be null");
        }

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

    @GetMapping(value = "/invitations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
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

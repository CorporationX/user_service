package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.FilteredResponse;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.util.goal.validator.GoalInvitationControllerValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal/invitation")
public class GoalInvitationController {

    private final GoalInvitationControllerValidator goalInvitationValidator;
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto goalInvitationDto) {
        goalInvitationValidator.checkInviterAndInvitedAreTheSame(goalInvitationDto);

        return goalInvitationService.createInvitation(goalInvitationDto);
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GoalInvitationDto acceptInvitation(@PathVariable Long id) {
        return goalInvitationService.acceptInvitation(id);
    }

    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public GoalInvitationDto rejectInvitation(@PathVariable Long id) {
        return goalInvitationService.rejectInvitation(id);
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public FilteredResponse getInvitations(@RequestBody InvitationFilterDto invitationFilterDto) {
        return new FilteredResponse(goalInvitationService.getInvitations(invitationFilterDto));
    }
}

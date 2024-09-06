package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goalInvitation")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/createInvitation")
    @ResponseStatus(HttpStatus.CREATED)
    public void createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        goalInvitationService.createInvitation(goalInvitationDto);
    }

    @PatchMapping("/acceptGoalInvitation")
    @ResponseStatus(HttpStatus.OK)
    public void acceptGoalInvitation(@RequestParam("id") long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PatchMapping("/acceptGoalInvitation")
    @ResponseStatus(HttpStatus.OK)
    public void rejectGoalInvitation(@RequestParam("id") long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("/getInvitations")
    public List<GoalInvitationDto> getInvitations(@RequestBody GoalInvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}

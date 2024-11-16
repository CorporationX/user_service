package school.faang.user_service.controller.goal;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.SuccessResponse;
import school.faang.user_service.dto.GoalInvitationResponseDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/GoalInvitation")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping()
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitationDTO) {
        return goalInvitationService.createInvitation(invitationDTO);
    }

    @PutMapping("/{id}")
    public GoalInvitationResponseDto acceptGoalInvitation(@PathVariable long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse rejectGoalInvitation(@PathVariable long id) {
        if(goalInvitationService.rejectGoalInvitation(id)) {
            return new SuccessResponse("Invitation declined.");
        } else {
            throw new IllegalArgumentException("Invitation does not exist.");
        }
    }

    @GetMapping()
    public List<GoalInvitation> getInvitationsByFilter(@RequestBody GoalInvitationFilterDto filterDTO) {
        return goalInvitationService.getInvitationsByFilter(filterDTO);
    }
}


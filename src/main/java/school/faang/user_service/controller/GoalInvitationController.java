package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public GoalInvitation createInvitation(@Valid GoalInvitationDto goalInvitationDto) {
        return goalInvitationService.createInvitation(goalInvitationDto);
    }

    @PostMapping
    public GoalInvitation acceptGoalInvitation(long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PostMapping
    public GoalInvitation rejectGoalInvitation(long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitation> getInvitations(InvitationFilterIDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}

package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final UserService userService;
    private final GoalRepository goalRepository;
    private final GoalInvitationRepository goalInvitationRepository;

    public void createInvitation(GoalInvitationDto invitation) {
        User inviter = userService.findUserById(invitation.getInviterId());
        User invited = userService.findUserById(invitation.getInvitedUserId());

        if (inviter.equals(invited)) {
            throw new IllegalArgumentException("Invalid request. Inviter and invited users must be different");
        }

        Optional<Goal> goal = goalRepository.findById(invitation.getGoalId());

        if (goal.isPresent()) {
            goalInvitationRepository.save(new GoalInvitation(
                    invitation.getId(),
                    goal.get(),
                    inviter,
                    invited,
                    invitation.getStatus(),
                    LocalDateTime.now(),
                    LocalDateTime.now()));
        } else {
            throw new EntityNotFoundException("Invalid request. Requester goal not found");
        }
    }
}

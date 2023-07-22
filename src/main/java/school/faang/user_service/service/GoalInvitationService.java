package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
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

    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitation(id);

        User invitedUser = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();

        validateGoalInvitation(invitedUser, goal);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(goalInvitation);
        invitedUser.getGoals().add(goal);
    }

    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = findGoalInvitation(id);

        if (goalRepository.existsById(goalInvitation.getGoal().getId())) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
            goalInvitationRepository.save(goalInvitation);
        } else {
            throw new EntityNotFoundException("Invalid request. Requested goal not found");
        }
    }

    private void validateGoalInvitation(User user, Goal goal) {
        if (!goalRepository.existsById(goal.getId())) {
            throw new EntityNotFoundException("Invalid request. Requested goal not found");
        } else if (user.getGoals().size() >= 3) {
            throw new IllegalArgumentException("The user already has the maximum number of goals");
        } else if (user.getGoals().contains(goal)) {
            throw new IllegalArgumentException("The user is already working on this goal");
        }
    }

    private GoalInvitation findGoalInvitation(long id) {
        return goalInvitationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid request. Requested goal invitation not found"));
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        if (invitations.isEmpty()) {
            return new ArrayList<>();
        }

        invitationFilters.stream()
                .filter(f -> f.isApplicable(filters))
                .forEach(f -> f.apply(invitations.stream(), filters));

        return invitations.stream().map(goalInvitationMapper::toDto).toList();
    }
}

package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalRepository goalRepository;
    private final GoalInvitationRepository goalInvitationRepository;

    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid request. Requested goal invitation not found"));

        User invitedUser = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();

        validateGoalInvitation(invitedUser, goal);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(goalInvitation);
        invitedUser.getGoals().add(goal);
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
}

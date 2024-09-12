package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalInvitationService goalInvitationService;

    private final GoalStatus ACTIVE_GOAL_STATUS = GoalStatus.ACTIVE;

    public void deactivateActiveUserGoalsAndDeleteIfNoOneIsWorkingWith(User user) {
        user.getGoals().stream()
                .filter(goal -> goal.getStatus().equals(ACTIVE_GOAL_STATUS))
                .forEach(goal -> {
                    List<GoalInvitation> goalInvitations = goal.getInvitations();

                    goal.getUsers().remove(user);
                    if (goal.getUsers().isEmpty()) {
                        goalInvitationService.deleteGoalInvitations(goalInvitations);
                        goalRepository.deleteById(goal.getId());
                    } else {
                        goalInvitationService.deleteGoalInvitationForUser(goalInvitations, user);
                    }
                });

        user.getGoals().clear();
    }

}

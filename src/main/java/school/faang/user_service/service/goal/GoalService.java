package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public void removeUserGoals(long userId) {
        //this whole thing doesnt account for parent and child goals
        List<Goal> userGoals = goalRepository.findGoalsByUserId(userId).toList();
        List<Long> goalIdsToBeDeleted = new ArrayList<>();

        userGoals.forEach(goal -> {
            List<User> goalUsers = goal.getUsers();

            goalUsers.removeIf(goalUser -> goalUser.getId() == userId);
            if (goal.getUsers().isEmpty()) {
                goalRepository.deleteById(goal.getId());
            } else {
                goal.setUsers(goalUsers);
            }
        });
    }

    public void setUserGoalsToSelf(User mentee) {
        List<Goal> userGoals = mentee.getGoals();
        userGoals.forEach(goal -> {
            goal.setMentor(mentee);
        });
    }
}

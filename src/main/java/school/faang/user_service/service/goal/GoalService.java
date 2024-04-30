package school.faang.user_service.service.goal;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

public interface GoalService {
    Stream<Goal> findGoalsByUserId(long userId);

    Goal create(String title, String description, long parent);

    int countActiveGoalsPerUser(long userId);

    Stream<Goal> findByParent(long goalId);

    int countUsersSharingGoal(long goalId);

    List<User> findUsersByGoalId(long goalId);

    void removeSkillsFromGoal(long goalId);

    void addSkillToGoal(long skillId, long goalId);
}

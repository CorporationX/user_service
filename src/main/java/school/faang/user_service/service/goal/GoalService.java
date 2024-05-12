package school.faang.user_service.service.goal;

import school.faang.user_service.entity.goal.Goal;

public interface GoalService {

    Goal findGoalById(long id);

    int findActiveGoalsByUserId(long id);

    void delete(Goal goal);
}

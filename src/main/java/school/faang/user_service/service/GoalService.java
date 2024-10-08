package school.faang.user_service.service;

import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalService {
    void removeGoals(List<Goal> goalIds);
}

package school.faang.user_service.controller.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalServiceImpl;

import java.util.List;

@Component
public class GoalController {
    private GoalServiceImpl goalServiceImpl;

    private GoalDto createGoal(Long userId, Goal goal){
        return goalServiceImpl.createGoal(userId, goal);
    }

    private GoalDto updateGoal(Long goalId, GoalDto goal){
        return goalServiceImpl.updateGoal(goalId, goal);
    }
    private void deleteGoal(long goalId){
        goalServiceImpl.deleteGoal(goalId);
    }
    private List<GoalDto> findSubtasksByGoalId(long goalId){
        return goalServiceImpl.findSubtasksByGoalId(goalId);
    }
    private List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter){
        return goalServiceImpl.getGoalsByUser(userId, filter);
    }
}

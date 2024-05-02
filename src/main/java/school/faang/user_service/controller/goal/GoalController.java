package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalServiceImpl;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalController {
    private final GoalServiceImpl goalServiceImpl;
    private final GoalValidator goalValidator;

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
        goalValidator.validateThatIdIsGreaterThan0(goalId);
        return goalServiceImpl.findSubtasksByGoalId(goalId);
    }
    private List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto goalFilterDto){
        goalValidator.validateThatIdIsGreaterThan0(userId);
        return goalServiceImpl.findGoalsByUserId(userId, goalFilterDto);
    }
}

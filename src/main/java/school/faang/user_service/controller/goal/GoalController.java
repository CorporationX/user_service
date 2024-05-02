package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final GoalValidator goalValidator;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoalTitle(goalDto);
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidator.validateGoalTitle(goalDto);
        return goalService.updateGoal(goalId, goalDto);
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(userId, filter);
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }
}

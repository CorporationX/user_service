package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void deleteGoal(Long goalId) {
        if (isValidId(goalId)) {
            throw new DataValidationException("Invalid ID: " + goalId);
        }
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        if (isValidId(userId)) {
            throw new DataValidationException("Incorrect data");
        }
        return goalService.getGoalsByUser(userId, filter);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId) {
        if (isValidId(goalId)) {
            throw new DataValidationException("Incorrect data");
        }
        return goalService.findSubtasksByGoalId(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto goalFilterDto) {
        if (isValidId(goalId)) {
            throw new DataValidationException("Incorrect data");
        }
        return goalService.findSubtasksByGoalId(goalId, goalFilterDto);
    }

    private boolean isValidId(Long id) {
        return id == null || id <= 0;
    }
}
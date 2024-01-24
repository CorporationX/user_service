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
        if (goalId == null || goalId <= 0) {
            throw new DataValidationException("Invalid ID: " + goalId);
        } else {
            goalService.deleteGoal(goalId);
        }
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        if (userId == null || userId <= 0) {
            throw new DataValidationException("Incorrect data");
        }
        return goalService.getGoalsByUser(userId, filter);
    }
}
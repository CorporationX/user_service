package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void createGoal(Long userId, Goal goal) {
        if (goal.getTitle().isBlank()) {
            throw new DataValidationException("Goals title must exists");
        }
        goalService.createGoal(userId, goal);
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        if (goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Goals title must exists");
        }
        goalService.updateGoal(goalId, goalDto);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }
}

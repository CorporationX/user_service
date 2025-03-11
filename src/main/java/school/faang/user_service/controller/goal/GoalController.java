package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalDtoValidator;
import school.faang.user_service.validator.goal.GoalValidator;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoalDto(goalDto);
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidator.validateGoalDto(goalDto);
        return goalService.updateGoal(goalId, goalDto);
    }

    public GoalDto deleteGoal(long goalId) {
        goalValidator.validateGoal(goalId);
        return GoalDto;
    }
}
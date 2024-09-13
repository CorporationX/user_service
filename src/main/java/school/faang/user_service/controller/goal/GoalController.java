package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalServiceImpl;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalServiceImpl goalServiceImpl;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        return goalServiceImpl.createGoal(userId, goalDto);
    }

    public void deleteGoal(Long goalId) {
        goalServiceImpl.deleteGoal(goalId);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        return goalServiceImpl.updateGoal(goalId, goalDto);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filter) {
        return goalServiceImpl.findSubtasksByGoalId(goalId, filter);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalServiceImpl.getGoalsByUser(userId, filter);
    }
}

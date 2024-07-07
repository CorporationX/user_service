package school.faang.user_service.controller.goal;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.dto.GoalFilterDto;

import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
public class GoalController {
    private final GoalService goalService;

    public void createGoal(@NonNull Long userId, @NonNull Goal goal) {
        String title = goal.getTitle();
        int MAX_LENGTH_TITLE = 64;

        if (title.isBlank()) {
            throw new NullPointerException("Goal is null");
        } else if (title.length() >= MAX_LENGTH_TITLE) {
            throw new NullPointerException("The length of the goal name is longer than allowed");
        } else if (goalService.findAllGoalTitles().contains(goal.getTitle())) {
            throw new NullPointerException("A goal with the same name already exists");
        } else {
            goalService.createGoal(userId, goal);
        }
    }

    public void updateGoal(@NonNull Long goalId, @NonNull GoalDto goal) {
        goalService.updateGoal(goalId, goal);
    }

    public void deleteGoal(@NonNull Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getSubtasksByGoalId(@NonNull Long goalId, GoalFilterDto filters) {
        return goalService.getSubtasksByGoalId(goalId, filters);
    }

    public List<GoalDto> getGoalsByUser(@NonNull Long userId, GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }

}

package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.validator.GoalValidator;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;
    private final UserContext userContext;

    @PutMapping("/goals/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goal) {
        goalValidator.validateUserId(goalId);
        goalValidator.validateGoalTitle(goal);
        return goalService.updateGoal(goalId, goal);
    }

    @PostMapping("/goals")
    public GoalDto createGoal(@RequestBody GoalDto goal) {
        long userId = userContext.getUserId();
        goalValidator.validateUserId(userId);
        goalValidator.validateGoalTitle(goal);
        goalValidator.validateDescription(goal);
        return goalService.createGoal(userId, goal);
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
    }


    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        goalValidator.validateFilter(filter);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

}

package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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


    public GoalDto updateGoal(Long goalId, GoalDto goal) {
        goalValidator.validateUserId(goalId);
        goalValidator.validateGoalTitle(goal);
        return goalService.updateGoal(goalId, goal);
    }

    @Operation(summary = "Покупка премиума", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "id пользователя", required = true)})
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

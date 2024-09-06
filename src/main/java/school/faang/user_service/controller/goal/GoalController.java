package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.validation.group.Create;
import school.faang.user_service.dto.validation.group.Update;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapping.GoalMapper;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.GoalValidator;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalValidator goalValidator;
    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public void createGoal(@RequestBody @Validated(Create.class) GoalDto goalDto) {
        goalValidator.validateCreation(goalDto);
        var goal = goalMapper.toEntity(goalDto);
        goalService.createGoal(goal, goalDto.getUserId());
    }

    @PutMapping
    public void updateGoal(@RequestBody @Validated(Update.class) GoalDto goalDto) {
        goalValidator.validateUpdating(goalDto);
        var goal = goalMapper.toEntity(goalDto);
        goalService.updateGoal(goal);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/sub-goals/{parentGoalId}")
    public List<GoalDto> findSubGoalsByParentGoalId(@PathVariable Long parentGoalId, GoalFilterDto filter) {
        List<Goal> foundGoals = goalService.findSubGoalsByParentGoalId(parentGoalId, filter);
        return goalMapper.toDtos(foundGoals);
    }

    @GetMapping("/user/{userId}")
    public List<GoalDto> findGoalsByUser(@PathVariable long userId, GoalFilterDto filter) {
        List<Goal> foundGoals = goalService.findGoalsByUserId(userId, filter);
        return goalMapper.toDtos(foundGoals);
    }
}

package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapping.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public GoalResponseDto createGoal(@RequestBody @Valid CreateGoalDto dto) {
        Goal goal = goalMapper.toEntity(dto);

        Long userId = dto.getUserId();
        Long parentGoalId = dto.getParentGoalId();
        List<Long> skillIds = dto.getSkillIds();

        Goal createdGoal = goalService.createGoal(goal, userId, parentGoalId, skillIds);

        return goalMapper.toDto(createdGoal);
    }

    @PatchMapping
    public GoalResponseDto updateGoal(@RequestBody @Valid UpdateGoalDto dto) {
        Goal goal = goalMapper.toEntity(dto);
        Goal updatedGoal = goalService.updateGoal(goal, dto.getSkillIds());
        return goalMapper.toDto(updatedGoal);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PostMapping("/sub-goals/{parentGoalId}")
    public List<GoalResponseDto> findSubGoalsByParentGoalId(@PathVariable Long parentGoalId, @RequestBody GoalFilterDto filter) {
        List<Goal> foundGoals = goalService.findSubGoalsByParentGoalId(parentGoalId, filter);
        return goalMapper.toDtos(foundGoals);
    }

    @PostMapping("/user/{userId}")
    public List<GoalResponseDto> findGoalsByUser(@PathVariable long userId, @RequestBody GoalFilterDto filter) {
        List<Goal> foundGoals = goalService.findGoalsByUserId(userId, filter);
        return goalMapper.toDtos(foundGoals);
    }
}

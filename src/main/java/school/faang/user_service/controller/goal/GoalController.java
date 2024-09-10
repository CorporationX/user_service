package school.faang.user_service.controller.goal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goals")
@Slf4j
public class GoalController {
    private final GoalService goalService;
    private final GoalMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(
            @RequestParam Long userId,
            @RequestBody GoalDto goalDto) {
        Goal goal = mapper.toEntity(goalDto);
        Goal newGoal = goalService.createGoal(userId, goal);
        return mapper.toDto(newGoal);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(@RequestBody GoalDto goalDto) {
        Goal goal = mapper.toEntity(goalDto);
        Goal newGoal = goalService.updateGoal(goal);
        return mapper.toDto(newGoal);
    }

    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> findSubtaskByGoalId(@PathVariable Long goalId) {
        List<Goal> subtasks = goalService.findSubtaskByGoalId(goalId);
        return mapper.toDtoList(subtasks);
    }
}

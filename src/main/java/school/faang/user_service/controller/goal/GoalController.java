package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.service.impl.goal.GoalServiceImpl;

import java.util.List;

@RestController("api/v1/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalServiceImpl goalServiceImpl;

    @PostMapping("/userId/{userId}")
    public GoalDto createGoal(@PathVariable @Positive long userId, @RequestBody @Validated GoalDto goalDto) {
        return goalServiceImpl.createGoal(userId, goalDto);
    }

    @PutMapping("/goalId/{goalId}")
    public GoalDto update(@PathVariable @Positive long goalId, @RequestBody @Validated GoalDto goalDto) {
        return goalServiceImpl.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/goalId/{goalId}")
    public void deleteGoal(@PathVariable @Positive long goalId) {
        goalServiceImpl.deleteGoal(goalId);
    }

    @GetMapping("/goalId/{goalId}")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable @Positive long goalId, @RequestBody GoalFilterDto filterDto) {
        return goalServiceImpl.findSubtasksByGoalId(goalId, filterDto);
    }

    @GetMapping("/userId/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable @Positive long userId, @RequestBody GoalFilterDto filterDto) {
        return goalServiceImpl.getGoalsByUser(userId, filterDto);
    }
}

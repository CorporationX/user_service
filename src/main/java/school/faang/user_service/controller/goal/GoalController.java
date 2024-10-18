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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.dto.goal.GoalNotificationDto;
import school.faang.user_service.service.impl.goal.GoalServiceImpl;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalServiceImpl goalService;

    @PostMapping("/userId/{userId}")
    public GoalDto createGoal(@PathVariable @Positive long userId, @RequestBody @Validated(CreateGroup.class) GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/goalId/{goalId}")
    public GoalDto update(@PathVariable @Positive long goalId, @RequestBody @Validated(UpdateGroup.class) GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/goalId/{goalId}")
    public void deleteGoal(@PathVariable @Positive long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/goalId/{goalId}")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable @Positive long goalId, @RequestBody GoalFilterDto filterDto) {
        return goalService.findSubtasksByGoalId(goalId, filterDto);
    }

    @GetMapping("/userId/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable @Positive long userId, @RequestBody GoalFilterDto filterDto) {
        return goalService.getGoalsByUser(userId, filterDto);
    }

    @GetMapping("/{goalId}")
    public GoalNotificationDto getGoal(@PathVariable long goalId) {
        return goalService.getGoal(goalId);
    }
}

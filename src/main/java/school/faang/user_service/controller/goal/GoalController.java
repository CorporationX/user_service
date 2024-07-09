package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping(value = "{userId}/goals/create")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@Valid @PathVariable Long userId, @Valid @RequestBody GoalDto goalDto) {
        goalService.createGoal(userId, goalDto);
        return goalDto;
    }

    @PutMapping(value = "/goals/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(@Valid @PathVariable Long goalId, @Valid @RequestBody GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);
        return goalDto;
    }

    @DeleteMapping(value = "/goals/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGoal(@Valid @PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping(value = "/goals/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    private List<GoalDto> findSubtaskByGoalId(@Valid @PathVariable Long goalId, @Valid @RequestBody GoalFilterDto filters) {
        return goalService.findSubtaskByGoalId(goalId, filters);
    }

    @GetMapping(value = "/users/{userId}/goals")
    @ResponseStatus(HttpStatus.OK)
    private List<GoalDto> findGoalsByUser(@Valid @PathVariable Long userId, @Valid @RequestBody GoalFilterDto filters) {
        return goalService.findGoalsByUserId(userId, filters);
    }
}

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.dto.goal.UpdateGoalDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/goals")
public class GoalController {
    private final GoalService goalService;

    @DeleteMapping("{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseGoalDto createGoal(@RequestParam Long userId, @Valid @RequestBody CreateGoalDto goal){
        return goalService.createGoal(userId, goal);
    }

    @GetMapping("/filtered")
    public List<GoalDto> getSubGoalsByFilter(@RequestParam Long parentId, GoalFilterDto filterDto) {
        return goalService.getSubGoalsByFilter(parentId, filterDto);
    }

    @GetMapping("/owner/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, GoalFilterDto filterDto) {
        return goalService.getGoalsByUser(userId, filterDto);
    }

    @PutMapping
    public UpdateGoalDto updateGoal(@Valid @RequestBody UpdateGoalDto updateGoalDto){
        return goalService.updateGoal(updateGoalDto);
    }
}
package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping("goal")
    @ResponseStatus(value = HttpStatus.CREATED)
    public GoalDto createGoal(@RequestParam Long userId, @RequestBody @Valid GoalDto goalDto) {
        return goalService.create(userId, goalDto);
    }

    @PutMapping("goal")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public GoalDto updateGoal(@RequestBody @Valid UpdateGoalDto goalDto) {
        return goalService.update(goalDto);
    }

    @DeleteMapping("goal/{id}")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void deleteGoal(@PathVariable @NotNull long id) {
        goalService.delete(id);
    }

    @GetMapping("{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalDto> findSubtasksByGoalId(@PathVariable long id,
                                              @RequestParam(required = false) String title,
                                              @RequestParam(required = false) String status) {
        return goalService.findSubtasksByGoalId(id, title, status);
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<GoalDto> findGoalsByUserIdAndFilter(@PathVariable long id,
                                                    @RequestParam(required = false) String title,
                                                    @RequestParam(required = false) String status) {
        return goalService.findGoalsByUserIdAndFilter(id, title, status);
    }
}

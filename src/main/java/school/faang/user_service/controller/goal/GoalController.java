package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}

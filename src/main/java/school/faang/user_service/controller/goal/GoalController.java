package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    GoalDto create(@RequestParam long userId, @Valid @RequestBody GoalDto goalDto) {
        return goalService.create(userId, goalDto);
    }

    @PutMapping("/{goalId}")
    GoalDto update(@PathVariable long goalId, @Valid @RequestBody GoalDto goalDto) {
        return goalService.update(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    void delete(@PathVariable long goalId) {
        goalService.delete(goalId);
    }
}

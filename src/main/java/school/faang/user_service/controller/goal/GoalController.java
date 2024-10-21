package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/goal")
@Validated
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping
    public GoalDto createGoal(@RequestParam Long userId, @Valid @RequestBody GoalDto goalDto) {
        goalService.createGoal(userId, goalDto);
        return goalDto;
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @Valid @RequestBody GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);
        return goalDto;
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(
            @PathVariable long goalId,
            @RequestParam(required = false) String titleFilter) {
        return goalService.findSubtasksByGoalId(goalId, titleFilter);
    }

    @GetMapping("/{userId}")
    public List<GoalDto> getGoalsByUser(
            @PathVariable Long userId,
            @ModelAttribute GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }

}
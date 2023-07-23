package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal")
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public GoalDto createGoal(@RequestParam @NotEmpty(message = "Title can't be empty") String title, Long userId,
                              List<String> skills){

        GoalDto goal = new GoalDto(0L, title);

        return goalService.createGoal(goal, userId, skills);
    }

    @PutMapping
    public GoalDto updateGoal(@RequestParam @NotEmpty(message = "Title can't be empty") String title, Long userId,
                              List<String> skills){

        GoalDto goal = new GoalDto(0L, title);

        return goalService.updateGoal(goal, userId, skills);
    }

    @DeleteMapping
    public GoalDto deleteGoal(Long goalId){
        return goalService.deleteGoal(goalId);
    }
}

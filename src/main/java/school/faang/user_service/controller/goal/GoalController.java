package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("api/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{goalId}/complete/{userId}")
    public void completeUserGoal(@PathVariable long userId,
                                 @PathVariable long goalId) {
        goalService.completeUserGoal(userId, goalId);
    }
}
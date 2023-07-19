package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void deleteGoal(long goalId){
        goalService.deleteGoal(goalId);
    }
}

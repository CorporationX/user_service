package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.goal.Goal;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.goal.GoalService;


@Component
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    public void createGoal(Long userId, Goal goal){
        // TODO: check fo dublicate
        if (goal.getTitle() == null){
            System.out.println("You need to write Title for Goal");
        } else {
            goalService.createGoal(userId, goal);
        }
    }
}

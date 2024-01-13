package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@Controller
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    public void createGoal(Long userId, Goal goal) {
        //Проверяем на наличие названия
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            return;
        }
        //Сохраняем в БД
        goalService.createGoal(userId, goal);
    }
}

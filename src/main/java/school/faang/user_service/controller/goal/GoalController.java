package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
private final GoalService goalService;

    public void updateGoal(Long goalId, GoalDto goal){
        //Проверить на наличие названия
    }
}

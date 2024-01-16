package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
private final GoalService goalService;

    public void updateGoal(Long goalId, GoalDto goal){
        //Проверить на наличие названия
        if (goal.getTitle() == null || goal.getTitle().isEmpty()){
            throw new DataValidationException("Goal title is empty");
        }
        goalService.updateGoal(goalId, goal);
    }
}

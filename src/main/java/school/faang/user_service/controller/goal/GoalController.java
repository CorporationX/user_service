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

    public GoalDto updateGoal(Long goalId, GoalDto goal){
        if (goal.getTitle() == null || goal.getTitle().isEmpty()){
            throw new DataValidationException("Goal title is empty");
        }
        return goalService.updateGoal(goalId, goal);
    }
}

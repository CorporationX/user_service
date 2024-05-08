package school.faang.user_service.controller.goal;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validation.goal.GoalValidation;

@Data
@RestController
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final GoalValidation goalValidation;

    public void createGoal(Long userId, GoalDto goal) {
        if (goalValidation.isValidateByEmptyTitle(goal)) {
            goalService.createGoal(userId, goal);
        }
    }



}

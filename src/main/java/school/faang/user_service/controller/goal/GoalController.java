package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

/**
 * @author Ilia Chuvatkin
 */

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;

    public void createGoal(Long userId, GoalDto goal) {
        goalValidator.validateTitle(goal);
        if (userId != null) {
            goalService.createGoal(userId, goal);
        }
    }
}

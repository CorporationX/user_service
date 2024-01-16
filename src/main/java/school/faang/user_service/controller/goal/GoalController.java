package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;


    public GoalDto createGoal(Long userId, GoalDto goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            throw new DataValidationException("Название цели не должно быть пустым");
        }
        return goalService.createGoal(userId, goal);
    }
}

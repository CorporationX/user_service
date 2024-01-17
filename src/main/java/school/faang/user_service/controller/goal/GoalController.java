package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}

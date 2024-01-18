package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final GoalValidator goalValidator;

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        goalValidator.validateFilter(filter);
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

}

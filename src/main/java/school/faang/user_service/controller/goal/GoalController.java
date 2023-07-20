package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        validate(userId, filter);
        return service.getGoalsByUser(userId, filter);
    }

    public List<GoalDto> getSubGoalsByUser(long userId, GoalFilterDto filter) {
        validate(userId, filter);
        return service.getSubGoalsByUser(userId, filter);
    }

    private void validate (long userId, GoalFilterDto filter) throws DataValidationException {
        if (userId < 1) throw new DataValidationException("userId can not be less than 1");
    }
}

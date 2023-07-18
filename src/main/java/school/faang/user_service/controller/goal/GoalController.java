package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.Exeptions.DataValidationException;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public void updateGoal(Long goalId, GoalDto goal) throws DataValidationException {
        if (goal.getTitle() == null || goal.getTitle().isBlank())
            throw new DataValidationException("Title can not be blank or null");
        service.updateGoal(goalId, goal);
    }
}

package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exсeption.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    public GoalDto updateGoal(long id, GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank())
            throw new DataValidationException("Title can not be blank or null");
        return service.updateGoal(id, goalDto);
    }
}

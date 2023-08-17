package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;
    private final GoalValidator validator;

    @PutMapping("/create/goal")
    public GoalDto createGoal(GoalDto goalDto) {
        validator.createGoalControllerValidation(goalDto);
        return service.createGoal(goalDto);
    }
}

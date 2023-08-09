package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PutMapping("/goals/{goalId}")
    public GoalDto updateGoal(@PathVariable long id, @RequestBody GoalDto goalDto) {
        validator.updateGoalControllerValidation(goalDto);
        return service.updateGoal(id, goalDto);
    }
}

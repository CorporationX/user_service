package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.GoalService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public UpdateGoalDto updateGoal(UpdateGoalDto updateGoalDto){
       return goalService.updateGoal(updateGoalDto);
    }
}

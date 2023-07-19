package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.service.goal.GoalService;


@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public ResponseGoalDto createGoal(Long userId, CreateGoalDto goal){
      return goalService.createGoal(userId, goal);
    }
}

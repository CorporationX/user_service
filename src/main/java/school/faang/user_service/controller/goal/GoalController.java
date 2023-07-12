package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        return goalService.getGoalsByUser(userId, filterDto);
    }

    public Goal createGoal(Long userId, Goal goal){
      return goalService.createGoal(userId, goal);
    }
}

package school.faang.user_service.controller.goal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/api/v1/goal")
public class GoalController {
    private GoalService goalService;

    @PostMapping("/create")
    public GoalDto create(@RequestBody CreateGoalDto createGoalDto) {
        return goalService.create(createGoalDto);
    }
}

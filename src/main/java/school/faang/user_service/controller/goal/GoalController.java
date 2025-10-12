package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public GoalDto create(@Valid CreateGoalDto createGoalDto) {
        return goalService.create(createGoalDto);
    }

    public GoalDto update(long goalId, @Valid UpdateGoalDto updateGoalDto) {
        return goalService.update(goalId, updateGoalDto);
    }

    public void delete(long goalId) {
        goalService.delete(goalId);
    }

    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        return goalService.getByFilters(filters);
    }
}
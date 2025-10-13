package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.facade.goal.GoalFacade;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {

    private final GoalFacade goalFacade;

    @PostMapping
    public GoalDto create(@Valid @RequestBody CreateGoalDto createGoalDto) {
        return goalFacade.create(createGoalDto);
    }

    @PatchMapping("/{goalId}")
    public GoalDto update(@PathVariable long goalId, @Valid @RequestBody GoalUpdateDto updateGoalDto) {
        return goalFacade.update(goalId, updateGoalDto);
    }

    @DeleteMapping("/{goalId}")
    public void delete(@PathVariable long goalId) {
        goalFacade.delete(goalId);
    }

    @PostMapping("/filters")
    public List<GoalDto> getByFilters(@Valid @RequestBody GoalFilterDto filters) {
        return goalFacade.filters(filters);
    }
}

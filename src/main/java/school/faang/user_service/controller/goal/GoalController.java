package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/goal")
    public GoalDto create(@RequestBody CreateGoalDto createGoalDto) {
        log.info("Validate a new goal");
        validateTitle(createGoalDto.title());
        validateDescription(createGoalDto.description());
        validateUsersIds(createGoalDto.userIds());
        log.info("The goal '{}' is valid", createGoalDto.title());
        return goalService.create(createGoalDto);
    }

    @PutMapping("/goal/{goalId}")
    public GoalDto update(@PathVariable long goalId, @RequestBody UpdateGoalDto updateGoalDto) {
        log.info("Validate the goal #{}", goalId);
        validateTitle(updateGoalDto.title());
        validateDescription(updateGoalDto.description());
        log.info("The goal #{} is valid", goalId);
        return goalService.update(goalId, updateGoalDto);
    }

    @DeleteMapping("goal/{goalId}")
    public void delete(@PathVariable long goalId) {
        goalService.delete(goalId);
    }

    @PostMapping("/goals")
    public List<GoalDto> getByFilters(@RequestBody GoalFilterDto goalFilterDto) {
        return goalService.getByFilters(goalFilterDto);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DataValidationException("The goal title must exist and be non-empty");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DataValidationException("The goal description must exist and be non-empty");
        }
    }

    private void validateUsersIds(List<Long> usersIds) {
        if (usersIds == null || usersIds.isEmpty()) {
            throw new DataValidationException("User`s IDs must exist and be non-empty");
        }
    }
}

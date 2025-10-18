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
    private static final String SUCCESSFULLY_LOG = "Successfully";
    private static final String COMPLETED_LOG = "Validation completed";

    private final GoalService goalService;

    @PostMapping("/goal")
    public GoalDto create(@RequestBody CreateGoalDto createGoalDto) {
        log.info("Try to create a new goal in the Controller");
        validateTitle(createGoalDto.title());
        validateDescription(createGoalDto.description());
        validateUsersIds(createGoalDto.userIds());
        log.info(COMPLETED_LOG);
        return goalService.create(createGoalDto);
    }

    @PutMapping("/goal/{goalId}")
    public GoalDto update(@PathVariable long goalId, @RequestBody UpdateGoalDto updateGoalDto) {
        log.info("Try to update the goal in the Controller");
        validateTitle(updateGoalDto.title());
        validateDescription(updateGoalDto.description());
        log.info(COMPLETED_LOG);
        return goalService.update(goalId, updateGoalDto);
    }

    @DeleteMapping("goal/{goalId}")
    public void delete(@PathVariable long goalId) {
        log.info("Try to delete the goal in the Controller");
        goalService.delete(goalId);
        log.info(SUCCESSFULLY_LOG);
    }

    @PostMapping("/goals")
    public List<GoalDto> getByFilters(@RequestBody GoalFilterDto goalFilterDto) {
        log.info("Try to filter goals in the Controller");
        return goalService.getByFilters(goalFilterDto);
    }

    private void validateTitle(String title) {
        log.info("Goal title validation");
        if (title == null || title.isBlank()) {
            log.error("Invalid goal title");
            throw new DataValidationException("The goal title must exist and be non-empty");
        } else {
            log.info(SUCCESSFULLY_LOG);
        }
    }

    private void validateDescription(String description) {
        log.info("Goal description validation");
        if (description == null || description.isBlank()) {
            log.error("Invalid goal description");
            throw new DataValidationException("The goal description must exist and be non-empty");
        } else {
            log.info(SUCCESSFULLY_LOG);
        }
    }

    private void validateUsersIds(List<Long> usersIds) {
        log.info("User`s IDs validation");
        if (usersIds == null || usersIds.isEmpty()) {
            log.error("Invalid user`s IDs");
            throw new DataValidationException("User`s IDs must exist and be non-empty");
        }
        log.info(SUCCESSFULLY_LOG);
    }
}

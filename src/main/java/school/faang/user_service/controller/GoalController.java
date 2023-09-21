package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
@Validated
@Slf4j
public class GoalController {

    private final GoalService goalService;
    private final UserContext userContext;

    @GetMapping("/{id}")
    public GoalDto getGoal(@PathVariable Long id) {
        log.info("Received request to get goal with id: {}", id);
        return goalService.getGoalById(id);
    }

    @PostMapping("{id}/filter")
    public List<GoalDto> getGoalsByParent(@PathVariable Long id, @RequestBody GoalFilterDto filter) {
        log.info("Received request to get goals for user: {} with filter: {}", id, filter);
        return goalService.getGoalsByParent(id, filter);
    }


    @PostMapping("/filter")
    public List<GoalDto> getGoalsByUser(@RequestBody GoalFilterDto filter) {
        Long userId = userContext.getUserId();
        log.info("Received request to get goals for user: {} with filter: {}", userId, filter);
        return goalService.getGoalsByUser(userId, filter);
    }

    @PostMapping
    public GoalDto create(@Valid @RequestBody GoalDto goalDto) {
        Long userId = userContext.getUserId();
        log.info("Received request to create goal: {} for user: {}", goalDto, userId);
        return goalService.create(userId, goalDto);
    }

    @PutMapping("/{id}")
    public GoalDto update(@PathVariable Long id, @Valid @RequestBody GoalDto goalDto) {
        log.info("Received request to update goal with id: {}, target: {}", id, goalDto);
        return goalService.update(id, goalDto);
    }
}

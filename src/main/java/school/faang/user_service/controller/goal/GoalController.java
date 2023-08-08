package school.faang.user_service.controller.goal;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validation.GoalValidator;

import java.util.List;

@Tag(name = "Управление целями")
@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @Operation(summary = "Получить цели пользователя по идентификатору")
    @GetMapping("/users/{userId}/goals")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filters) {
        List<GoalDto> goals = goalService.getGoalsByUser(userId, filters);
        return goals;
    }

    @Operation(summary = "Добавить цель пользователю по идентификатору")
    @PostMapping("/users/{userId}/goals")
    public ResponseEntity<GoalDto> createGoal(@PathVariable Long userId, @RequestBody GoalDto goal) {
        GoalDto createdGoal = goalService.createGoal(userId, goal);
        return new ResponseEntity<>(createdGoal, HttpStatus.CREATED);
    }

    @Operation(summary = "Получить подцели цели по идентификатору с фильтром")
    @PostMapping("/subtasks/{goalId}")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable long goalId, @RequestBody GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @Operation(summary = "Удалить цель")
    @DeleteMapping("/goal/{goalId}")
    public void deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
    }
}

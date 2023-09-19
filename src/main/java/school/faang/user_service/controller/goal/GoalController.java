package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.util.Message;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public GoalDto createGoal(@RequestBody GoalDto goalDto, Long userId){
        return goalService.createGoal(goalDto, userId);
    }

    @PutMapping
    public GoalDto updateGoal(@RequestBody GoalDto goalDto, Long userId){
        return goalService.updateGoal(goalDto, userId);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable Long goalId){
        if (goalId < 0){
            throw new DataValidationException(Message.GOAL_NOT_FOUND);
        }

        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{userId}")
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto goalFilterDto){
        return goalService.getGoalsByUser(userId, goalFilterDto);
    }

    @GetMapping("/{parentGoalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(Long parentGoalId, GoalFilterDto goalFilterDto){
        return goalService.findSubtasksByGoalId(parentGoalId, goalFilterDto);
    }

    @PutMapping("/{goalId}/complete")
    public GoalDto completeGoal(@PathVariable Long goalId){
        return goalService.completeGoal(goalId);
    }
}
package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void deleteGoal(Long goalId) {
        validateId(goalId);
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        validateId(userId);
        return goalService. getGoalsByUser(userId, filter);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId) {
        validateId(goalId);
        return goalService.findSubtasksByGoalId(goalId);
    }

    public List<GoalDto> retrieveFilteredSubtasksForGoal(Long goalId, GoalFilterDto goalFilterDto) {
        validateId(goalId);
        return goalService.retrieveFilteredSubtasksForGoal(goalId, goalFilterDto);
    }//retrieveFilteredSubtasksForGoal        ПЕРЕИМЕНННОВАТЬ

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        validateId(goalId);
        if(goalDto.getTitle() == null || goalDto.getTitle().isBlank()){
            throw new DataValidationException("Invalid title: " + goalDto.getTitle());
        }
       return  goalService.updateGoal(goalId,goalDto);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Invalid ID: " + id);
        }
    }
}
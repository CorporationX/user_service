package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static school.faang.user_service.utils.GlobalValidator.validateOptional;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;
    public static final int MAX_USER_ACTIVE_GOALS = 3;

    public void validateGoalCreation(Long userId, GoalDto goalDto) {
        validateTitle(goalDto.getTitle());
        int countActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        validateUserGoalsCount(countActiveGoals);
        validateSkills(goalDto.getSkillIds());
    }

    public void validateGoalUpdate(Long goalId, GoalDto goalDto) {
        validateTitle(goalDto.getTitle());
        validateGoalExists(goalId);
        validateGoalStatus(goalId);
        validateSkills(goalDto.getSkillIds());
    }

    public void validateGoalExists(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Goal does not exist");
        }
    }

    private void validateGoalStatus(Long goalId) {
        Goal foundedGoal = validateOptional(goalRepository.findById(goalId), String.format("Goal with ID %d not found", goalId));
        if (foundedGoal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("Completed goals can't be updated");
        }
    }

    private void validateUserGoalsCount(int countActiveGoals) {
        if (countActiveGoals >= MAX_USER_ACTIVE_GOALS) {
            throw new DataValidationException("User can't have more than 3 active goals");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Goal must have title");
        }
    }

    private void validateSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new DataValidationException("Goal must have at least one skill");
        }
        skillIds.forEach(this::validateSkill);
    }

    private void validateSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException(String.format("Skill with id \"%s\" does not exist", skillId));
        }
    }
}

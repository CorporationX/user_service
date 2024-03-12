package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidation {
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;

    public void validateGoalCreate(Long userId, GoalDto goalDto, int maxUserActiveGoal) {
        validateMaximumGoalsPerUser(userId, maxUserActiveGoal);
        validateTitle(goalDto.getTitle());
        validateSkills(goalDto.getSkillIds());
    }

    public void validateGoalUpdate(Long goalId, GoalDto goalDto) {
        validateTitle(goalDto.getTitle());
        validateExistGoal(goalId);
        validateStatus(goalId);
        validateSkills(goalDto.getSkillIds());
    }

    private void validateStatus(Long goalId) {
        Goal foundedGoal = goalRepository.findById(goalId).get();
        if (GoalStatus.COMPLETED.equals(foundedGoal.getStatus())) {
            throw new DataValidationException(GoalConstraints.GOAL_STATUS_COMPLETED.getMessage());
        }
    }

    public void validateExistGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new DataValidationException(GoalConstraints.GOAL_NOT_FOUND.getMessage());
        }
    }

    private void validateTitle(String title) {
        if (title == null) {
            throw new DataValidationException(GoalConstraints.GOAL_TITLE_NULL.getMessage());
        }
        if (title.isBlank()) {
            throw new DataValidationException(GoalConstraints.GOAL_TITLE_EMPTY.getMessage());
        }
    }

    private void validateSkills(List<Long> skillsId) {
        if (skillsId == null || skillsId.isEmpty()) {
            throw new DataValidationException(GoalConstraints.GOAL_NOT_HAVING_SKILL.getMessage());
        }
        skillsId.forEach(this::validateSkill);
    }

    private void validateSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new DataValidationException(String.format(GoalConstraints.SKILL_NOT_FOUND.getMessage(), skillId));
        }
    }

    private void validateMaximumGoalsPerUser(Long userId, int maxUserActiveGoal) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= maxUserActiveGoal) {
            throw new DataValidationException(GoalConstraints.GOAL_MAXIMUM_PER_USER.getMessage());
        }
    }
}

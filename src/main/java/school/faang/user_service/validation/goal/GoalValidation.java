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
        if (foundedGoal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("Status goal is already: \"Completed\"");
        }
    }

    public void validateExistGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new DataValidationException("Goal does not exist");
        }
    }

    private void validateTitle(String title) {
        if (title == null) {
            throw new DataValidationException("Goal title can't be null");
        }
        if (title.isBlank()) {
            throw new DataValidationException("Goal title can't be empty");
        }
    }

    private void validateSkills(List<Long> skillsId) {
        if (skillsId == null || skillsId.isEmpty()) {
            throw new DataValidationException("Goal must have skill");
        }
        skillsId.forEach(this::validateSkill);
    }

    private void validateSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new DataValidationException(String.format("Skill with id \"%s\" does not exist", skillId));
        }
    }

    private void validateMaximumGoalsPerUser(Long userId, int maxUserActiveGoal) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= maxUserActiveGoal) {
            throw new DataValidationException("Maximum number of user goals reached");
        }
    }
}

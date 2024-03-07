package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.EntityFieldsException;
import school.faang.user_service.exception.EntityUpdateException;
import school.faang.user_service.exception.goal.UserReachedMaxGoalsException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;

    public void validateGoalCreation(Long userId, GoalDto goalDto, int maxUserActiveGoals) {
        validateTitle(goalDto.getTitle());
        int countActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        validateUserGoalsCount(countActiveGoals, maxUserActiveGoals);
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
        Goal foundedGoal = goalRepository.findById(goalId).get();
        if (foundedGoal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new EntityUpdateException("Completed goals can't be updated");
        }
    }

    private void validateUserGoalsCount(int countActiveGoals, int maxUserActiveGoals) {
        if (countActiveGoals >= maxUserActiveGoals) {
            throw new UserReachedMaxGoalsException("User can't have more than 3 active goals");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isEmpty() || title.isBlank()) {
            throw new EntityFieldsException("Goal must have title");
        }
    }

    private void validateSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new EntityFieldsException("Goal must have at least one skill");
        }
        skillIds.forEach(this::validateSkill);
    }

    private void validateSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException(String.format("Skill with id \"%s\" does not exist", skillId));
        }
    }
}

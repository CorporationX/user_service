package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

/**
 * @author Ilia Chuvatkin
 */

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillService skillService;

    public void validateActiveGoals(long countActiveGoals) {
        if (countActiveGoals > MAX_ACTIVE_GOALS) {
            throw new DataValidationException("Too many active goals!");
        }
    }

    public void validateExistingSkills(List<Long> userSkillsIds, GoalDto goal) {
        if (!userSkillsIds.containsAll(goal.getSkillIds())) {
            throw new DataValidationException("Not enough skills for the goal!");
        }
    }

    public void validateTitle(GoalDto goal) {
        String title = goal.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Title is empty!");
        }
    }

    public void validateUserId(Long userId) {
        if (userId == null) {
            throw new DataValidationException("User ID required!");
        }
    }

    public void validateForCreate(Long userId, GoalDto goalDto) {
        List<Long> userSkillsIds = skillService.getUserSkills(userId).stream().map(SkillDto::getId).toList();
        long countActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        validateExistingSkills(userSkillsIds, goalDto);
        validateActiveGoals(countActiveGoals);
    }
    public void validateTitleAndGoalId(Long goalId, GoalDto goal) {
        String title = goal.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Title is empty!");
        }
        if (goalId == null) {
            throw new DataValidationException("Goal ID is null!");
        }
    }

    public void validateByExistingSkills(Goal goal) {
        if (!goal.getSkillsToAchieve().stream().allMatch(s -> skillRepository.existsByTitle(s.getTitle()))) {
            throw new DataValidationException("Some skills do not exist in database!");
        }
    }

    public void validateByCompleted(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Goal was completed!");
        }
    }

    public void validateUserIdAndFilter(Long userId, GoalFilterDto filter) {
        if (userId == null) {
            throw new DataValidationException("User ID is null!");
        }
        if (filter == null) {
            throw new DataValidationException("Filter is null!");
        }
    }

    public void validateGoalId(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Goal with id = " + goalId + " is not exists");
        }
    }
}

package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
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
}

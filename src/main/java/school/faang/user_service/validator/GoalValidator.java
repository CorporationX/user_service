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


    public Boolean isValidateByActiveGoals(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) <= MAX_ACTIVE_GOALS) {
            return true;
        }
        throw new DataValidationException("Too many active goals!");
    }

    public Boolean isValidateByExistingSkills(Long userId, GoalDto goal) {
        List<Long> userSkillsIds = skillService.getUserSkills(userId).stream().map(SkillDto::getId).toList();
        if (userSkillsIds.containsAll(goal.getSkillIds())) {
            return true;
        }
        throw new DataValidationException("Not enough skills for the goal!");
    }

    public Boolean isValidateByEmptyTitle(GoalDto goal) {
        if (!goal.getTitle().trim().isEmpty()) {
            return true;
        }
        throw new DataValidationException("Title is empty!");
    }
}

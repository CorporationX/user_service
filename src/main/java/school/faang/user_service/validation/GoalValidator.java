package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private static final int MAX_COUNT_ACTIVE_GOALS_PER_USER = 3;

    public void validateGoal(Long userId, GoalDto goal) throws GoalValidationException{
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new GoalValidationException("Goal title cannot be empty");
        }

        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_COUNT_ACTIVE_GOALS_PER_USER) {
            throw new GoalValidationException("User cannot have more than 3 active goals");
        }

        List<Long> skillIds = goal.getSkillIds();
        if (skillIds == null || skillIds.isEmpty()) {
            throw new GoalValidationException("Goal must have at least one skill");
        }

        List<Skill> existingSkills = skillRepository.findAllById(skillIds);
        if (existingSkills.size() < skillIds.size()) {
            throw new GoalValidationException("One or more skills do not exist");
        }
    }

    public void validateGoalBeforeUpdate(Goal goal) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new GoalValidationException("Goal already completed");
        }

        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        if (skillsToAchieve != null) {
            for (Skill skill : skillsToAchieve) {
                if (!skillRepository.existsByTitle(skill.getTitle())) {
                    throw new GoalValidationException("Skill " + skill.getTitle() + " does not exist");
                }
            }
        }
    }
}
package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.goal.GoalFieldsException;
import school.faang.user_service.exception.goal.UserReachedMaxGoalsException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;

    public void validateGoal(Long userId, GoalDto goalDto, int maxUserActiveGoals) {
        if (goalDto.getTitle() == null) {
            throw new GoalFieldsException("Goal title can't be null");
        }
        if (goalDto.getTitle().isEmpty()) {
            throw new GoalFieldsException("Goal title can't be empty");
        }
        int countActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (countActiveGoals >= maxUserActiveGoals) {
            throw new UserReachedMaxGoalsException("User can't have more than 3 active goals");
        }
        validateSkills(goalDto.getSkillIds());
    }

    private void validateSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new GoalFieldsException("Goal must have at least one skill");
        }
        skillIds.forEach(this::validateSkill);
    }

    private void validateSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException(String.format("Skill with id \"%s\" does not exist", skillId));
        }
    }
}

package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private static final int GOALS_PER_USER = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public void validateGoalTitle(GoalDto goalDto) throws ValidationException {
        if (goalDto.getTitle().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
    }

    public void validateGoalsPerUser(Long userId) throws ValidationException {
        if (goalRepository.countActiveGoalsPerUser(userId) > GOALS_PER_USER) {
            throw new ValidationException("There cannot be more than 3 active goals per user");
        }
    }

    public void validateGoalSkills(List<Long> skillIds) throws ValidationException {
        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new ValidationException("Cannot create goal with non-existent skills");
        }
    }
}

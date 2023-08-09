package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exсeption.DataValidationException;
import school.faang.user_service.exсeption.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public void updateGoalControllerValidation(GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Title can not be blank or null");
        }
    }

    public void updateGoalServiceValidation(long id, GoalDto goalDto) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("Goal already completed!");
        }

        List<Long> skillIds = goalDto.getSkillIds();
        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new EntityNotFoundException("Goal contains non-existent skill!");
        }
    }
}

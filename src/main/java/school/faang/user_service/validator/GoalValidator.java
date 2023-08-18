package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exсeption.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
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
    private final GoalMapper goalMapper;
    private static final int MAX_ACTIVE_GOALS = 3;

    public void createGoalControllerValidation(GoalDto goalDto) {
        if (goalDto == null) {
            throw new DataValidationException("Goal cannot be null");
        }

    public void updateGoalControllerValidation(GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Title can not be blank or null");
        }
    }

    public void creatingGoalServiceValidation(long userId, GoalDto goalDto) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_ACTIVE_GOALS) {
            String maxActiveGoalsExceptionText = "User can't have more than " + MAX_ACTIVE_GOALS + " Active goals";
            throw new DataValidationException(maxActiveGoalsExceptionText);
        }
        List<Skill> skillsToAchieve = goalMapper.toEntity(goalDto).getSkillsToAchieve();
        skillsToAchieve.forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new DataValidationException("Contains a non-existence skill");
            }
        });
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

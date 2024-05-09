package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private static final int MAX_COUNT_ACTIVE_GOALS = 3;

    public void validateBeforeCreate(Long userId, GoalDto goalDto) {
        int userGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (userGoalsCount >= MAX_COUNT_ACTIVE_GOALS) {
            throw new DataValidationException("Пользователь не может иметь более " +
                    MAX_COUNT_ACTIVE_GOALS + " активных целей");
        }
        validateSkills(goalDto);
    }

    public void validateBeforeUpdate(Goal goal, GoalDto goalDto) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Цель уже завершена");
        }
        validateSkills(goalDto);
    }

    public void validateGoalTitle(GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Название цели не должно быть пустым");
        }
    }

    private void validateSkills(GoalDto goalDto) {
        goalDto.getSkillIds().forEach((skillId) -> {
            if (!skillService.existsById(skillId)) {
                throw new DataValidationException("Навык с id " + skillId + " не существует");
            }
        });
    }
}

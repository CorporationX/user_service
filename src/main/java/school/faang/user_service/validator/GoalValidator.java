package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;

import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final GoalRepository goalRepository;
    private final SkillService skillService;

    private static final int MAX_GOALS_PER_USER = 3;

    public void validate(long userId, GoalDto goalDto) {
        if (goalRepository.countActiveGoalsPerUser(userId) == MAX_GOALS_PER_USER) {
            throw new DataValidationException("Достигнуто максимальное количество целей");
        }
        if (!goalDto.getSkillIds().stream().allMatch(skillService::existsById)) {
            throw new DataValidationException("Некорректные скиллы");
        }
    }


    public void validateUserId(Long userId) {
        if (userId == null) {
            throw new DataValidationException("Пользователь не найден");
        }
    }

    public void validateGoalTitle(GoalDto goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            throw new DataValidationException("Название цели не должно быть пустым");
        }
    }

    public void validateSkills(List<Skill> skills) {
        if (!skills.stream()
                .allMatch(skillService::validateSkill)) {
            throw new DataValidationException("Некорректные скиллы");
        }
    }

    public void validateFilter(GoalFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Filter cannot be null");
        }
    }
}

package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final GoalService goalService;
    private final SkillService skillService;

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
}

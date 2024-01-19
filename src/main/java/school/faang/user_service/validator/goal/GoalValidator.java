package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;

@Component
@RequiredArgsConstructor
public class GoalValidator {
private final GoalService goalService;
private final SkillService skillService;

public void validate(long userId, GoalDto goalDto, int maxGoalsPerUser) {
    if (goalService.countActiveGoalsPerUser(userId) == maxGoalsPerUser) {
        throw new DataValidationException("Достигнуто максимальное количество целей");
    }
    if (!goalDto.getSkillIds().stream().allMatch(skillService::existsById)) {
        throw new DataValidationException("Некорректные скиллы");
    }
}
}

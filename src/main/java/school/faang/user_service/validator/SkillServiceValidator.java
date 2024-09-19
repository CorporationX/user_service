package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillServiceValidator {

    private final SkillService skillService;

    public void validateExistByTitle(final List<Skill> skills) {
        validateCondition(
                !skillService.existsByTitle(skills),
                "There is no skill with this name"
        );
    }

    private void validateCondition(boolean condition, String errorMessage) {
        if (condition) {
            throw new DataValidationException(errorMessage);
        }
    }
}

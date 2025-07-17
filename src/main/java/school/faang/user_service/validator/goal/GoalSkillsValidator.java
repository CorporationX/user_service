package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalSkillsValidator implements Validator<Goal, GoalValidationParams> {

    @Override
    public ValidationResult validate(Goal goal, GoalValidationParams validationParams) {
        List<Long> goalRqSkills = validationParams.goalRq().getSkillIds();

        goalRqSkills.removeAll(goal.getSkillsToAchieve().stream().map(Skill::getId).toList());

        if (!goalRqSkills.isEmpty()) {
            return new ValidationResult(false, List.of(new Violation(ErrorCode.SKILL_NOT_EXISTS,
                    new ErrorField(validationParams.path("skillIds"), "body", goalRqSkills.toString(), null))));
        }

        return new ValidationResult(true, List.of());
    }
}

package school.faang.user_service.validator.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GoalDeadlineValidator implements Validator<Goal, GoalValidationParams> {

    @Override
    public ValidationResult validate(Goal goal, GoalValidationParams validationParams) {
        if (goal.getDeadline() != null && goal.getDeadline().isBefore(LocalDateTime.now())) {
            return new ValidationResult(false, List.of(new Violation(ErrorCode.DEADLINE_IN_PAST,
                    new ErrorField(validationParams.path("deadline") + ".deadline", "body", goal.getDeadline().toString(), null))));
        }
        return new ValidationResult(true, List.of());
    }
}

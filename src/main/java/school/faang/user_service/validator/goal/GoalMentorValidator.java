package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalMentorValidator implements Validator<Goal, GoalValidationParams> {

    @Override
    public ValidationResult validate(Goal goal, GoalValidationParams validationParams) {
        if (validationParams.goalRq().getMentorId() != null) {
            ErrorField errorField = new ErrorField(
                    validationParams.path("mentorId") , "body", validationParams.goalRq().getMentorId().toString(), null);
            if (goal.getMentor() == null) {
                return new ValidationResult(false, List.of(new Violation(ErrorCode.MENTOR_NOT_EXISTS, errorField)));
            } else if (!goal.getMentor().isActive()) {
                return new ValidationResult(false, List.of(new Violation(ErrorCode.MENTOR_NOT_ACTIVE, errorField)));
            } else if (goal.getMentor().getId().equals(validationParams.userId())) {
                return new ValidationResult(false, List.of(new Violation(ErrorCode.MENTOR_EQUAL_USER, errorField)));
            }
        }

        return new ValidationResult(true, List.of());
    }
}

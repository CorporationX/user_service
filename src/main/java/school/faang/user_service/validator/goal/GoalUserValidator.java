package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.service.UserServiceImpl;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalUserValidator implements Validator<Goal, GoalValidationParams> {
    private final UserServiceImpl userService;

    @Override
    public ValidationResult validate(Goal goal, GoalValidationParams validationParams) {
        if (validationParams.userId() > 0) {
            User user = userService.getUserEntityById(validationParams.userId());
            ErrorField errorField = new ErrorField(
                    validationParams.path("userId"), "body", validationParams.userId().toString(), null);
            if (user.getId() == null) {
                return new ValidationResult(false, List.of(new Violation(ErrorCode.USER_NOT_EXISTS, errorField)));
            } else if (!user.isActive()) {
                return new ValidationResult(false, List.of(new Violation(ErrorCode.USER_NOT_ACTIVE, errorField)));
            }
        }

        return new ValidationResult(true, List.of());
    }

    @Override
    public boolean applicable(Goal goal, GoalValidationParams validationParams) {
        return validationParams.userId() != null;
    }
}

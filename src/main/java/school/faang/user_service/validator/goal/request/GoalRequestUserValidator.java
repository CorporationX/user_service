package school.faang.user_service.validator.goal.request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalRequest;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Component
public class GoalRequestUserValidator implements Validator<GoalRequest, GoalRequestValidationParams> {

    @Override
    public ValidationResult validate(GoalRequest rq, GoalRequestValidationParams params) {

        if (rq instanceof GoalCreateDto goalCreateRq && goalCreateRq.getUserId() == null) {
            return new ValidationResult(false, List.of(new Violation(ErrorCode.VALIDATION_REQUIRED,
                    new ErrorField(params.path("userId"), "body", null, "positive integer"))));
        }

        return new ValidationResult(true, List.of());
    }

    @Override
    public boolean applicable(GoalRequest rq, GoalRequestValidationParams params) {
        return rq instanceof GoalCreateDto;
    }
}

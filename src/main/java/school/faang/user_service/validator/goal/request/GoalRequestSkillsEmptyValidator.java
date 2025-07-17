package school.faang.user_service.validator.goal.request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalRequest;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Component
public class GoalRequestSkillsEmptyValidator implements Validator<GoalRequest, GoalRequestValidationParams> {

    @Override
    public ValidationResult validate(GoalRequest rq, GoalRequestValidationParams params) {

        if (rq instanceof GoalDto goalRq && goalRq.getSkillIds() != null && goalRq.getSkillIds().isEmpty()) {
            return new ValidationResult(false, List.of(new Violation(ErrorCode.VALIDATION_EMPTY,
                    new ErrorField(params.path("skillIds"), "body", "[]", "array of integers"))));
        }

        return new ValidationResult(true, List.of());
    }

    @Override
    public boolean applicable(GoalRequest rq, GoalRequestValidationParams params) {
        return rq instanceof GoalDto;
    }
}

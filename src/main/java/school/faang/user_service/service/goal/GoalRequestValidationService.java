package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalRequest;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;
import school.faang.user_service.validator.goal.request.GoalRequestValidationParams;

import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.enums.ErrorCode.REQUEST_VALIDATION_ERROR;
import static school.faang.user_service.enums.ErrorCode.VALIDATION_REQUIRED;

@Service
@RequiredArgsConstructor
public class GoalRequestValidationService {
    private final List<Validator<GoalRequest, GoalRequestValidationParams>> validators;

    public void createGoal(GoalCreateDto goalCreateRq) {
        List<Violation> violations = new ArrayList<>();

        GoalRequestValidationParams validationParams = new GoalRequestValidationParams("");

        validators.stream()
                .filter(validator -> validator.applicable(goalCreateRq, validationParams))
                .forEach(validator -> {
                    ValidationResult validationResult = validator.validate(goalCreateRq, validationParams);
                    if (!validationResult.isValid()) {
                        violations.addAll(validationResult.violations());
                    }
                });

        violations.addAll(validateGoal(goalCreateRq.getGoal(), "goal"));

        if (!violations.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, REQUEST_VALIDATION_ERROR, violations);
        }
    }

    public void updateGoal(Long goalId, GoalDto goalUpdateRq) throws BusinessException {
        List<Violation> violations = new ArrayList<>();

        if (goalId == null) {
            violations.add(new Violation(VALIDATION_REQUIRED,
                    new ErrorField("goalId", "query", null, "positive integer")));
        }

        violations.addAll(validateGoal(goalUpdateRq, ""));

        if (!violations.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, REQUEST_VALIDATION_ERROR, violations);
        }
    }

    public void deleteGoal(Long goalId) throws BusinessException {
        if (goalId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, REQUEST_VALIDATION_ERROR, List.of(
                    new Violation(VALIDATION_REQUIRED,
                            new ErrorField("goalId", "query", null, "positive integer"))));
        }
    }

    private List<Violation> validateGoal(GoalDto goal, String path) {
        List<Violation> violations = new ArrayList<>();

        if (goal == null) {
            violations.add(new Violation(VALIDATION_REQUIRED,
                    new ErrorField(path, "body", null, "goal object")));
        } else {
            GoalRequestValidationParams validationParams = new GoalRequestValidationParams(path);

            validators.stream()
                    .filter(validator -> validator.applicable(goal, validationParams))
                    .forEach(validator -> {
                        ValidationResult validationResult = validator.validate(goal, validationParams);

                        if (!validationResult.isValid()) {
                            violations.addAll(validationResult.violations());
                        }
                    });

            if (goal.getSubGoals() != null) {
                for (int i = 0; i < goal.getSubGoals().size(); i++) {
                    violations.addAll(validateGoal(goal.getSubGoals().get(i), path + ".subGoals[" + i + "]"));
                }
            }
        }

        return violations;
    }
}

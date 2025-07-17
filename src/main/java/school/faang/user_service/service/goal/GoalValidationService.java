package school.faang.user_service.service.goal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;
import school.faang.user_service.validator.goal.GoalValidationParams;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalValidationService {
    private final List<Validator<Goal, GoalValidationParams>> validators;
    private final GoalRepository goalRepository;
    @Getter
    private final GoalRequestValidationService requestValidation;

    @Value("${user-service.goals.max-per-user}")
    private int maxGoalsPerUser;

    public void createGoal(Long userId, Goal goal, GoalDto goalRq, Goal parentGoal, String path) throws BusinessException {
        List<Violation> violations = new ArrayList<>();
        GoalValidationParams validationParams = new GoalValidationParams(userId, goalRq, path);

        if (userId != null && goalRepository.countActiveGoalsPerUser(userId) > maxGoalsPerUser) {
            violations.add(new Violation(ErrorCode.GOALS_MORE_THAN_MAXIMUM));
        }

        if (goal.getDeadline() != null && parentGoal.getDeadline() != null
                && goal.getDeadline().isAfter(parentGoal.getDeadline())) {
            violations.add(new Violation(ErrorCode.DEADLINE_GREATER_PARENT,
                    new ErrorField(path + "deadline", "body", goal.getDeadline().toString(), null)));
        }

        validateGoal(goal, validationParams, violations);
    }

    public void updateGoal(Long goalId, Goal storedGoal, GoalDto goalUpdateRq) {
        ErrorField errorField = new ErrorField("goalId", "query", goalId.toString(), null);
        GoalValidationParams validationParams = new GoalValidationParams(null, goalUpdateRq, "");

        if (storedGoal.getId() == null) {
            throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.DATA_NOT_FOUND,
                    List.of(new Violation(ErrorCode.GOAL_NOT_EXISTS, errorField)));
        }

        if (storedGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.BUSINESS_ERROR,
                    List.of(new Violation(ErrorCode.GOAL_COMPLETED, errorField)));
        }

        validateGoal(storedGoal, validationParams, new ArrayList<>());
    }

    private void validateGoal(Goal goal, GoalValidationParams params, List<Violation> violations) {
        validators.stream()
                .filter(validator -> validator.applicable(goal, params))
                .forEach(validator -> {
                    ValidationResult validationResult = validator.validate(goal, params);

                    if (!validationResult.isValid()) {
                        violations.addAll(validationResult.violations());
                    }
                });

        if (!violations.isEmpty()) {
            throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.BUSINESS_ERROR, violations);
        }
    }

}

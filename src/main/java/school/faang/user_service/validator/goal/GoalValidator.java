package school.faang.user_service.validator.goal;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final GoalRepository goalRepository;

    public void goalIdIsPositiveAndNotNullOrElseThrowValidationException(Long goalId) throws ValidationException {
        if (goalId == null) {
            throw new ValidationException("Goal id can't be null");
        }
        if (goalId < 0) {
            throw new ValidationException("Goal id can't be less than 0");
        }
    }

    public void goalIsExistedOrElseThrowException(Long goalId) throws ValidationException {
        goalRepository.findById(goalId).orElseThrow(() -> new ValidationException("Goal id " + goalId + " not exists"));
    }

    public void userActiveGoalsAreLessThenIncomingOrElseThrowException(Long userId, int goalMaxCount) throws ValidationException {
        if (goalRepository.countActiveGoalsPerUser(userId) >= goalMaxCount) {
            throw new ValidationException("User " + userId + " has max active goals");
        }
    }

    public void userNotWorkingWithGoalOrElseThrowException(Long userId, Long goalId) {
        goalRepository.findGoalsByUserId(userId)
                .mapToLong(Goal::getId)
                .filter(goal -> goal == goalId)
                .findAny()
                .ifPresent(goal -> {
                    throw new ValidationException("User with id " + userId + " already has goal with id " + goalId);
                });
    }
}

package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final UserRepository userRepository;
    private final GoalRepository repository;
    @Value("${service.goal.max-active-goals}")
    private int maxActiveGoals;

    public void validateUserId(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User with ID " + userId + "not found"));
    }

    public void validateCreationGoal(long userId) {
        validateUserId(userId);
        if (repository.countActiveGoalsPerUser(userId) >= maxActiveGoals) {
            throw new DataValidationException("User with ID " + userId + " can have maximum goal = " + maxActiveGoals);
        }
    }

    public Goal validateUpdate(long goalId) {
        Goal goal = repository.findById(goalId).orElseThrow(() ->
                new ResourceNotFoundException("Goal with ID " + goalId + " not found"));
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("You cannot update Goal " + goalId + "with the status " + goal.getStatus());
        }
        return goal;
    }

    public void validateGoalId(long goalId) {
        if (!repository.existsById(goalId)) {
            throw new ResourceNotFoundException("Goal with ID " + goalId + " not found");
        }
    }
}

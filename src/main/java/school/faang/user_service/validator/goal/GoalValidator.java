package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final UserRepository userRepository;
    private final GoalRepository repository;

    public void validateUserId(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User with ID " + userId + "not found"));
    }

    public void validateCreationGoal(long userId, int maxGoal) {
        if (repository.countActiveGoalsPerUser(userId) >= maxGoal) {
            throw new DataValidationException("User with ID " + userId + " can have maximum goal = " + maxGoal);
        }
    }

    public Goal validateUpdate(long goalId, GoalDto goalDto) {
        Goal goal = repository.findById(goalId).orElseThrow(() ->
                new ResourceNotFoundException("Goal with ID " + goalId + " not found"));
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("You cannot update Goal " + goalId + "with the status " + goal.getStatus());
        }

        if (goalDto.titleSkills().isEmpty()) {
            throw new DataValidationException("Skills cannot be empty");
        }
        return goal;
    }

    public void validateGoalId(long goalId) {
        if (!repository.existsById(goalId)) {
            throw new ResourceNotFoundException("Goal with ID " + goalId + " not found");
        }
    }
}

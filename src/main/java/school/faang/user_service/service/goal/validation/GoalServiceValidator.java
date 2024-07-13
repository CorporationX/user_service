package school.faang.user_service.service.goal.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.InvalidRequestParams;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalServiceValidator {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;


    public void validateCreation(long userId, GoalDto goalDto) {
        validateUserExistence(userId);
        validateActiveGoalsCount(userId);
        validateSkillsExistence(goalDto);
    }

    public void validateUpdating(long goalId, GoalDto goalDto) {
        validateGoalExistence(goalId);
        validateStatus(goalId);
        validateSkillsExistence(goalDto);
    }

    public void validateDeleting(long goalId) {
        validateGoalExistence(goalId);
    }

    public void validateFindingSubtasksByGoalId(long goalId) {
        validateGoalExistence(goalId);
    }

    public void validateFindingGoalsByUserId(long userId) {
        validateUserExistence(userId);
    }

    private void validateUserExistence(long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id {} does not exist", userId);
            throw new InvalidRequestParams("User not found");
        }
    }

    private void validateActiveGoalsCount(long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) > 2) {
            log.info("Active goals per user {} is greater than 2", userId);
            throw new InvalidRequestParams("Active goals count exceeded");
        }
    }

    private void validateSkillsExistence(GoalDto goalDto) {
        if (!goalDto.getSkillsToAchieveId().stream()
                .allMatch(skillRepository::existsById)) {
            log.info("Skill not found");
            throw new InvalidRequestParams("Skill not found");
        }
    }

    private void validateStatus(Long goalId) {
        if (goalRepository.findById(goalId).isPresent() && goalRepository.findById(goalId).get().getStatus() == GoalStatus.COMPLETED) {
            log.info("Goal {} is completed", goalId);
            throw new InvalidRequestParams("Goal already completed");
        }
    }

    private void validateGoalExistence(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            log.info("Goal {} does not exist", goalId);
            throw new InvalidRequestParams("Goal not found");
        }
    }
}

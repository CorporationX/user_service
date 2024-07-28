package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final UserService userService;
    private final SkillService skillService;
    private final GoalRepository goalRepository;
    @Value("${goal.max_active_goals:2}")
    private int maxActiveGoals;


    public void validateCreation(long userId, GoalDto goalDto) {
        validateUserExistence(userId);
        validateActiveGoalsCount(userId);
        validateSkillsExistence(goalDto);
    }

    public void validateUpdating(long goalId, GoalDto goalDto) {
        validateGoalExistence(goalId);
        if (goalDto.getStatus() != null) {
            validateStatus(goalId);
        }
        if (goalDto.getSkillsToAchieveIds() != null) {
            validateSkillsExistence(goalDto);
        }
    }

    public void validateUserExistence(long userId) {
        if (!userService.existsById(userId)) {
            log.info("User with id {} does not exist", userId);
            throw new DataValidationException("User not found");
        }
    }

    private void validateActiveGoalsCount(long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) > maxActiveGoals) {
            log.info("Active goals count exceeded for user {}", userId);
            throw new DataValidationException("Active goals count exceeded");
        }
    }

    private void validateSkillsExistence(GoalDto goalDto) {
        if (!goalDto.getSkillsToAchieveIds().stream()
                .allMatch(skillService::existsById)) {
            log.info("Skill not found");
            throw new DataValidationException("Skill not found");
        }
    }

    private void validateStatus(Long goalId) {
        if (goalRepository.findById(goalId).isPresent() && goalRepository.findById(goalId).get().getStatus() == GoalStatus.COMPLETED) {
            log.info("Goal {} already completed", goalId);
            throw new DataValidationException("Goal already completed");
        }
    }

    public void validateGoalExistence(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            log.info("Goal {} does not exist", goalId);
            throw new DataValidationException("Goal not found");
        }
    }
}

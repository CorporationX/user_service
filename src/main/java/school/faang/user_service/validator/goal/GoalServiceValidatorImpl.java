package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.data.DataNotMatchException;
import school.faang.user_service.exception.entity.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.skill.SkillServiceValidator;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalServiceValidatorImpl implements GoalServiceValidator {

    @Value("${limits.active-goals}")
    private int ACTIVE_GOALS_LIMIT;
    private final GoalRepository goalRepository;
    private final SkillServiceValidator skillServiceValidator;

    @Override
    @Transactional
    public void validateActiveGoalsLimit(Long userId) {
        if (ACTIVE_GOALS_LIMIT <= goalRepository.countActiveGoalsPerUser(userId)) {
            log.info("user with id:{} already have enough active goals", userId);
            throw new DataNotMatchException("already have enough active goals", userId);
        }
    }

    @Override
    public Goal existsById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("goal not found", goalId));
    }

    @Transactional
    @Override
    public void validateForUpdating(UpdateGoalDto goalDto) {
        Goal goal = existsById(goalDto.getId());
        checkGoalStatusForUpdate(goal);
        if(goalDto.getSkillsToAchieveIds() != null) {
            skillServiceValidator.validateSkillsExist(goalDto.getSkillsToAchieveIds());
        }
    }

    private void checkGoalStatusForUpdate(Goal goal) {
        if(GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new DataNotMatchException("goal enable for update, cause already completed", goal.getId());
        }
    }
}

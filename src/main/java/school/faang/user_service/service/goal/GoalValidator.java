package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    @Value("${app.goal.max-active-per-user}")
    private Integer maxExistedActiveGoals;

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public void validateCreation(GoalDto goalDto) {
        Long userId = goalDto.getUserId();
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        validateCommonRestrictions(goalDto);

        var alreadyExistedActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (alreadyExistedActiveGoals >= maxExistedActiveGoals) {
            throw new BadRequestException("User %s can have maximum %s goals", userId, maxExistedActiveGoals);
        }
    }

    public void validateUpdating(GoalDto goalDto) {
        validateCommonRestrictions(goalDto);

        Long goalId = goalDto.getGoalId();
        Goal goalFromDb = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));

        GoalStatus statusFromDb = goalFromDb.getStatus();
        if (statusFromDb == COMPLETED) {
            throw new BadRequestException("You cannot update Goal %s with the %s status.", goalId, statusFromDb);
        }

        GoalStatus statusFromDto = goalDto.getStatus();
        if (statusFromDto == COMPLETED && statusFromDb == ACTIVE && isEmpty(goalDto.getSkillIds())) {
            throw new BadRequestException("You cannot complete Goal %s with empty list of Goals.", goalId);
        }
    }

    private void validateCommonRestrictions(GoalDto goalDto) {
        List<Long> skillIds = goalDto.getSkillIds();
        if (skillIds != null && skillIds.size() != skillRepository.countExisting(skillIds)) {
            throw new BadRequestException("Skills from request are not presented in DB");
        }

        Long parentGoalId = goalDto.getParentGoalId();
        if (parentGoalId != null) {
            goalRepository.findById(parentGoalId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent Goal", parentGoalId));
        }
    }
}

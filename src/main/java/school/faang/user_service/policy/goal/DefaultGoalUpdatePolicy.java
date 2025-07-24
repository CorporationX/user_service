package school.faang.user_service.policy.goal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.AuthUserContext;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultGoalUpdatePolicy implements GoalUpdatePolicy {

    private final AuthUserContext authUserContext;
    private final GoalPolicyUtils goalPolicyUtils;

    @Override
    public void validate(UpdateGoalDto dto, Goal goal) {
        long currentUserId = authUserContext.getUserId();

        denyIfGoalComplete(dto, goal, currentUserId);

        goalPolicyUtils.denyIfNotMentorAndParticipant(
                currentUserId,
                goal,
                () -> deny("Only mentor or participant can update goal", dto, goal, currentUserId)
        );
    }

    private void denyIfGoalComplete(UpdateGoalDto dto, Goal goal, long currentUserId) {
        boolean isGoalCompleted = goal.getStatus() == GoalStatus.COMPLETED;
        if (isGoalCompleted) {
            deny("Cannot update goal", dto, goal, currentUserId);
        }
    }

    private void deny(String msg, UpdateGoalDto dto, Goal goal, long currentUserId) {
        String msgDetail = String.format(
                "Goal ID: %d, Status: %s, CurrentUserId: %d, DTO : %s",
                goal.getId(), goal.getStatus(), currentUserId, dto
        );
        throw new DataValidationException(msg, msg + ", " + msgDetail);
    }
}


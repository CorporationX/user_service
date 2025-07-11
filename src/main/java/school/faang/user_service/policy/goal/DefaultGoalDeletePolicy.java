package school.faang.user_service.policy.goal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultGoalDeletePolicy implements GoalDeletePolicy {

    private final UserContext userContext;
    private final GoalPolicyUtils goalPolicyUtils;

    @Override
    public void validate(Goal goal) {
        long currentUserId = userContext.getUserId();
        goalPolicyUtils.denyIfNotMentorAndParticipant(
                currentUserId,
                goal,
                () -> deny("Cannot update goal", goal, currentUserId)
        );
    }

    private void deny(String msg, Goal goal, long currentUserId) {
        String msgDetail = String.format(
                "Goal ID: %d, Status: %s, CurrentUserId: %d",
                goal.getId(), goal.getStatus(), currentUserId
        );
        throw DataValidationException.withCustomDebug(msg, msg + ", " + msgDetail);
    }
}


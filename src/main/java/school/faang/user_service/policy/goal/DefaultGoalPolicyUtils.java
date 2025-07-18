package school.faang.user_service.policy.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Component
public class DefaultGoalPolicyUtils implements GoalPolicyUtils {
    public void denyIfNotSelfAndMentee(
            Long currentUserId,
            List<Long> userIds,
            boolean isMentee,
            Runnable deny
    ) {
        boolean isSelf = userIds != null && userIds.contains(currentUserId);

        if (!isSelf && !isMentee) {
            deny.run();
        }
    }

    @Override
    public void denyIfNotMentorAndParticipant(
            Long currentUserId,
            Goal goal,
            Runnable deny
    ) {
        boolean isMentor = goal.getMentor() != null
                           && goal.getMentor().getId().equals(currentUserId);
        boolean isParticipant = goal.getUsers() != null
                                && goal.getUsers().stream()
                                        .map(User::getId)
                                        .anyMatch(
                                                userId -> userId.equals(currentUserId)
                                        );
        if (!isMentor && !isParticipant) {
            deny.run();
        }
    }

}

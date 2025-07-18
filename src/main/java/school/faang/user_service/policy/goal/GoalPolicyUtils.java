package school.faang.user_service.policy.goal;

import school.faang.user_service.entity.goal.Goal;

import java.util.List;

public interface GoalPolicyUtils {
    void denyIfNotSelfAndMentee(
            Long currentUserId,
            List<Long> userIds,
            boolean isMentee,
            Runnable deny
    );

    void denyIfNotMentorAndParticipant(
            Long currentUserId,
            Goal goal,
            Runnable deny
    );
}

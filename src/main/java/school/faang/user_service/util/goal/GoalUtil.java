package school.faang.user_service.util.goal;

import org.jetbrains.annotations.NotNull;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public class GoalUtil {

    public static void updateTime(Goal goalToUpdate, LocalDateTime time) {
        goalToUpdate.setUpdatedAt(time);
    }

    public static boolean isGoalActive(@NotNull Goal goal) {
        return GoalStatus.ACTIVE == goal.getStatus();
    }

}

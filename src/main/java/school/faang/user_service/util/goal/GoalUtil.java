package school.faang.user_service.util.goal;

import school.faang.user_service.entity.goal.Goal;

import java.time.LocalDateTime;

public class GoalUtil {

    public static void updateTime(Goal goalToUpdate, LocalDateTime time) {
        goalToUpdate.setUpdatedAt(time);
    }

}

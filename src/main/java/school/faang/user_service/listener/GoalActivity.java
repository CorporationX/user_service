package school.faang.user_service.listener;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;

@Component
public class GoalActivity implements Activity{
    private final Long rating = 2L;
    @Override
    public Long getUserId(Object object) {
        Goal goal = (Goal) object;
        return goal.getUsers().get(0).getId();
    }

    @Override
    public Long getRating(Object object) {
        return rating;
    }

    @Override
    public Class getEntityClass() {
        return Goal.class;
    }
}

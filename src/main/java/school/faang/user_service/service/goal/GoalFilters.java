package school.faang.user_service.service.goal;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoalFilters {
    private final List<GoalFilter> filters = new ArrayList<>();

    public void addGoalFilter(GoalFilter filter) {
        filters.add(filter);
    }
}

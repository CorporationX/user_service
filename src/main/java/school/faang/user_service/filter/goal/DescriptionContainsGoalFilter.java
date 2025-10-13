package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class DescriptionContainsGoalFilter implements FilterGoal {
    @Override
    public boolean isApplication(GoalFilterDto goalFilterDto) {
        return goalFilterDto.descriptionContains() != null
                && !goalFilterDto.descriptionContains().isBlank();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        String description = goalFilterDto.descriptionContains().toLowerCase();
        return goals.filter(goal -> goal.getDescription() != null
                && goal.getDescription().toLowerCase().contains(description));
    }
}

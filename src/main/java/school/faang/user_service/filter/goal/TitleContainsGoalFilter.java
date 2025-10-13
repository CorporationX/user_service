package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class TitleContainsGoalFilter implements FilterGoal {
    @Override
    public boolean isApplication(GoalFilterDto goalFilterDto) {
        return goalFilterDto.titleContains() != null
                && !goalFilterDto.titleContains().isBlank();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        String title = goalFilterDto.titleContains().toLowerCase();
        return goals.filter(goal -> goal.getTitle() != null
                && goal.getTitle().toLowerCase().equalsIgnoreCase(title));
    }
}

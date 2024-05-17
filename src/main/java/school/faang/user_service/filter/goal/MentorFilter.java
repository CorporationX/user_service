package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class MentorFilter implements GoalFilter {

    @Override
    public boolean isAcceptable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getMentor() != null;
    }

    @Override
    public Stream<Goal> applyFilter(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        return goals.filter(goal -> goal.getMentor().equals(goalFilterDto.getMentor()));
    }
}
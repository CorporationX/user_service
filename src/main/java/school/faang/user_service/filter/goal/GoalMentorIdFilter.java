package school.faang.user_service.filter.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
public class GoalMentorIdFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.mentorId() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        return goals
                .filter(goal ->
                        goal.getMentor() != null && Objects.equals(goal.getMentor().getId(), goalFilterDto.mentorId()));
    }
}

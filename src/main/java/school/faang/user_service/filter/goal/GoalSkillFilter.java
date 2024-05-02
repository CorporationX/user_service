package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;

import java.util.Objects;
import java.util.stream.Stream;

public class GoalSkillFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return Objects.nonNull(filter.getStatus());
    }

    @Override
    public void apply(Stream<GoalDto> goals, GoalFilterDto filter) {
        goals.filter(goalDto -> goalDto.getSkillIds().contains(filter.getSkillId()));
    }
}

package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;

import java.util.stream.Stream;

@Component
public interface GoalFilter {

    boolean isApplicable(GoalFilterDto filter);

    void apply(Stream<GoalDto> goals, GoalFilterDto filter);
}

package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public List<GoalDto> getGoalsByUser(@NotNull Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findAll().stream();
        goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .forEach(goalFilter -> goalFilter.applyFilter(goals,filters));

        return goals.map(goalMapper::toDto).toList();
    }
}

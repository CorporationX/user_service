package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    public List<GoalDto> getGoalsByUser(@NotNull Long userId, GoalFilters filters) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();
        List<GoalDto> goalDtos = goals.stream()
                .map(goalMapper::toDto)
                .toList();

        return filters.getFilters().stream()
                .reduce(goalDtos, (goals1, goalFilter) -> goalFilter.applyFilter(goals1), (a, b) -> b);
    }
}

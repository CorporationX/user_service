package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        validateId(userId);

        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);

        if (filter == null) {
            return goalStream.map(goalMapper::toDto).toList();
        }

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                goalStream = goalFilter.apply(goalStream, filter);
            }
        }

        return goalStream.map(goalMapper::toDto).toList();
    }

    private void validateId(Long userId) {
        if (userId == null) {
            throw new DataValidationException("User id cannot be null!");
        }
        if (userId <= 0) {
            throw new DataValidationException("User id cannot be less than 1!");
        }
    }
}

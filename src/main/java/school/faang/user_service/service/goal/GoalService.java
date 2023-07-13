package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.filter.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper = GoalMapper.INSTANCE;

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        GoalFilter goalFilter = new GoalFilter(filterDto, goalMapper);
        return goalFilter.filterGoals(goalRepository.findGoalsByUserId(userId));
    }
}

package school.faang.user_service.service.goal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

@Service
public interface GoalService {

    @Transactional
    GoalDto createGoal(Long userId, GoalDto goalDto);

    @Transactional
    GoalDto updateGoal(Long goalId, GoalDto goalDto);

    void deleteGoal(Long goalId);

    List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters);

    List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters);
}

package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    @Override
    @Transactional
    public Goal findGoalById(long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Goal with id %s not found", id)));
    }

    @Override
    public int findActiveGoalsByUserId(long id) {
        return goalRepository.countActiveGoalsPerUser(id);
    }

    @Override
    @Transactional
    public void delete(Goal goal) {
        goalRepository.delete(goal);
    }
}

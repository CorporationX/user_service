package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    @Transactional
    public void deleteGoalAndUnlinkChildren(Goal goal) {
        goal.getChildrenGoals()
                .forEach(child -> {
                    child.setParent(null);
                    goalRepository.save(child);
                });

        goalRepository.delete(goal);
    }
}

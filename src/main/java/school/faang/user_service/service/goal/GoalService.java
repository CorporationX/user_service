package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.goal.GoalRepository;


@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;


    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }
}

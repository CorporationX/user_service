package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exeptions.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Goal does not exist");
        }
        goalRepository.deleteById(goalId);
    }

}

package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;


@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;


    public void deleteGoal(long goalId) {
        if(!goalRepository.existsById(goalId)){
            throw new DataValidationException("Такой цели нет");
        }
        goalRepository.deleteById(goalId);
    }
}

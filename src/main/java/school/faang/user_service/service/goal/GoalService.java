package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public void deleteGoal(long goalID) {
        Optional<Goal> goal = goalRepository.findById(goalID);
        goal.ifPresentOrElse(goalRepository::delete,
                () -> {
                    throw new IllegalArgumentException("Invalid ID: " + goalID);
                });
    }
}
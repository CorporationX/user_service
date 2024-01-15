package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;


@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;


    public void deleteGoal(long goalID) {
        goalRepository.deleteById(goalID);
    }
}
package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public boolean existsById(Long goalId) {
        return goalRepository.existsById(goalId);
    }

    public long countActiveGoalsPerUser(Long userId) {
        return goalRepository.countActiveGoalsPerUser(userId);
    }
}
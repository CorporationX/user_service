package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skilllRepository;

    public List<Goal> findGoalsByUserId(Long id) {
        if (id < 1) throw new IllegalArgumentException("userId can not be less than 1");
        return goalRepository.findGoalsByUserId(id)
                .peek(goal -> goal.setSkillsToAchieve(skilllRepository.findSkillsByGoalId(goal.getId()))).toList();
    }
}

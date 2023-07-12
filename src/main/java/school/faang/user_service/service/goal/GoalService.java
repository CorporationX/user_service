package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final int MAX_GOALS_PER_USER = 3;

    public Goal createGoal(Long userId, Goal goal) {
        validateGoalToCreate(userId, goal);
        return goalRepository.save(goal);
    }

    private void validateGoalToCreate(Long userId, Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (goal.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS_PER_USER) {
            throw new IllegalArgumentException("Maximum number of goals for this user reached");
        }
        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        skillsToAchieve.forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new IllegalArgumentException("Skill not found");
            }
        });
    }
}

package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserGoalsValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final static int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillService skillService;

    public void createGoal(Long userId, Goal goal) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_ACTIVE_GOALS) {
            throw new UserGoalsValidationException("Max active goals doesnt grand then MAX_ACTIVE_GOALS");
        }
        if (!skillService.checkSkillsInDB(goal.getSkillsToAchieve())) {
            throw new DataValidationException("The goal contains non-existent skills");
        }
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getId());
        goal.getSkillsToAchieve()
                .forEach(skill -> goalRepository.addSkillToGoal(skill.getId(), goal.getId()));
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteGoalById(goalId);
    }
}

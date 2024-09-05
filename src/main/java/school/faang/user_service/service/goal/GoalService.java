package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

@Component
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_AMOUNT_ACTIVE_GOAL = 3;

    private final GoalRepository goalRepository;
    private final SkillService skillService;

    public void createGoal(Long userId, GoalDto goal) {
        validateGoalTitle(goal);
        validateUserActiveGoalCount(userId);
        validateGoalSkills(goal);

        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent());
        addSkillsToGoal(goal);
    }

    private void validateGoalTitle(GoalDto goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            throw new IllegalArgumentException("Goal title is empty or null");
        }
    }

    private void validateUserActiveGoalCount(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_AMOUNT_ACTIVE_GOAL) {
            throw new IllegalArgumentException("The number of active user targets has been exceeded");
        }
    }

    private void validateGoalSkills(GoalDto goal) {
        goal.getSkillIds()
                .forEach(skillId -> {
                    if (!skillService.isExistingSkill(skillId)) {
                        throw new IllegalArgumentException("Skill does not exist");
                    }
                });
    }

    private void addSkillsToGoal(GoalDto goal) {
        goal.getSkillIds()
                .forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
    }
}

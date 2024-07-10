package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.SkillService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalServiceValidate {
    private final SkillService skillService;
    private final static int MAX_NUMBERS_GOAL_USER = 3;

    public void validateCreateGoal(long userId, Goal goal, int countUserActive, List<String> allGoalTitles) {
        if (allGoalTitles.contains(goal.getTitle())) {
            throw new IllegalArgumentException("A goal with the same name already exists");
        } else if (countUserActive >= MAX_NUMBERS_GOAL_USER) {
            throw new IllegalStateException("This user " + userId + " has exceeded goal limit");
        } else if (!existByTitle(goal.getSkillsToAchieve())) {
            throw new IllegalArgumentException("There is no skill with this name");
        }
    }

    public void validateUpdateGoal(Goal goal, String status) {
        if (status.equals("COMPLETED")) {
            throw new IllegalStateException("Goal has already been achieved");
        } else if (!existByTitle(goal.getSkillsToAchieve())) {
            throw new IllegalArgumentException("There is no skill with this name");
        }
    }

    public void validateDeleteGoal(Stream<Goal> goal) {
        goal.findFirst().orElseThrow(() -> new NoSuchElementException("A goal with this ID does not exist"));
    }

    private boolean existByTitle(List<Skill> skills) {
        return skillService.existsByTitle(skills);
    }
}

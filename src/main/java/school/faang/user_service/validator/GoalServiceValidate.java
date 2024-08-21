package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.SkillService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalServiceValidate {
    private final SkillService skillService;
    private final static int MAX_NUMBERS_GOAL_USER = 3;

    public void checkDuplicateTitleGoal(GoalDto goal, List<String> allGoalTitles) {
        if (allGoalTitles.contains(goal.getTitle())) {
            throw new IllegalArgumentException("A goal with the same name already exists");
        }
    }

    public void checkLimitCountUser(int countUserActive) {
        if (countUserActive >= MAX_NUMBERS_GOAL_USER) {
            throw new IllegalStateException("This user has exceeded goal limit");
        }
    }

    public void checkExistenceGoal(Stream<Goal> goal) {
        goal.findFirst().orElseThrow(() -> new NoSuchElementException("A goal with this ID does not exist"));
    }

    public void checkStatusGoal(Goal goal) {
        if(goal.getStatus() == GoalStatus.COMPLETED)
            throw new IllegalStateException("The goal cannot be updated because it is already completed");
    }

    public void existByTitle(List<Skill> skills) {
        if (!skillService.existsByTitle(skills)) {
            throw new IllegalArgumentException("There is no skill with this name");
        }
    }
}
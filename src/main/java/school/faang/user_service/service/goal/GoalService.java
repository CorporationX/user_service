package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.entity.goal.Goal;


@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;


    public boolean isUserHasLessThanMaxGoal(Long userId, int maxValue) {
        if (goalRepository.countActiveGoalsPerUser(userId) > maxValue){
            System.out.println("There are already 3 active goals for user " + userId);
            return false;
        } else {
            return true;
        }
    }


    public boolean isGoalHasExistedSkills(Long userId, Goal goal){
        boolean isTrue = true;
        for (Skill s: goal.getSkillsToAchieve()) {
            if (!skillRepository.findAllByUserId(userId).contains(s)) {
                isTrue = false;
            }
        }
        return isTrue;
    }


    public void createGoal(Long userId, Goal goal) {
        if (isUserHasLessThanMaxGoal(userId, 3) && isGoalHasExistedSkills(userId, goal)) {
            goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        } else {
            System.out.println("Cannot create new goal for user " + userId);
        }

    }
}

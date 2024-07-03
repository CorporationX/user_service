package school.faang.user_service.controller.goal;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;


@Component
@Slf4j
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    public void createGoal(Long userId, Goal goal) {
        String title = goal.getTitle();
        int MAX_LENGTH_TITLE = 64;

        if (title == null) {
            log.error("Goal is null");
        } else if (title.length() >= MAX_LENGTH_TITLE) {
            log.error("The length of the goal name is longer than allowed");
        } else if (!goalService.findAllGoalTitles().contains(goal.getTitle())) {
            goalService.createGoal(userId, goal);
        } else {
            log.error("A goal with the same name already exists");
        }
    }
}

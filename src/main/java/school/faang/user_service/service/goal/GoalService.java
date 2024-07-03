package school.faang.user_service.service.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Service
@Slf4j
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final int MAX_NUMBERS_GOAL_USER = 3;

    @Autowired
    public GoalService(GoalRepository goalRepository, SkillService skillService) {
        this.goalRepository = goalRepository;
        this.skillService = skillService;
    }

    public List<String> findAllGoalTitles() {
        return goalRepository.findAllGoalTitles();
    }

    public void createGoal(Long userId, Goal goal) {
        List<Skill> skills = goal.getSkillsToAchieve();

        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_NUMBERS_GOAL_USER) {
            log.error("This user" + userId + "has exceeded goal limit");
        } else if (!skillService.existsByTitle(skills)) {
            log.error("There is no skill with this name");
        } else {
            goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
            skillService.create(skills, userId);
        }
    }
}
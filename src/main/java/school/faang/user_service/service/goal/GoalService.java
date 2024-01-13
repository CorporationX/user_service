package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

@Component
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;

    @Autowired
    public GoalService(GoalRepository goalRepository, SkillService skillService) {
        this.goalRepository = goalRepository;
        this.skillService = skillService;
    }

    public void createGoal(long userId, Goal goal) {
        //Проверяем что у пользователя не больше 3х целей
        if (goalRepository.countActiveGoalsPerUser(userId) >= 3) {
            return;
        }
        //Если используются только существующие скиллы (SkillService и SkillRepository)
        if (goal.getSkillsToAchieve().stream()
                .allMatch(skillService::validateSkill)) {
            //То сохраняем полученную цель в базу
            goalRepository.save(goal);
            //Сохраняем список навыков
            goal.getSkillsToAchieve().forEach(skillService::saveSkill);
            //Увеличить количество целей у юзера...
        }

    }
}

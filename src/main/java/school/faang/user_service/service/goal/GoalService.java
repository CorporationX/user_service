package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
public class GoalService {
private final GoalRepository goalRepository;

@Autowired
    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

public void createGoal(){
    //Проверяем что у пользователя не больше 3х целей
    //Проверяем что используются только существующие скиллы (SkillService и SkillRepository)
    //Сохраняем полученную цель в базу
}
}

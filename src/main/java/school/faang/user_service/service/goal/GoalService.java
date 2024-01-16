package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    public void updateGoal(Long goalId, GoalDto goal){
        //Получить текущую цель, проверить на наличие
        Optional<Goal> foundGoal = goalRepository.findById(goalId);
        if (foundGoal.isEmpty()){
            throw new DataValidationException("Цель не найдена");
        }

        // Перевести в сущность
        Goal updatedGoal = goalMapper.toEntity(goal);
        Goal currentGoal = foundGoal.get();

        // Произвести валидацию
        //Если завершает - то она не должна быть завершена
        if (currentGoal.getStatus() == GoalStatus.COMPLETED &&
        updatedGoal.getStatus() == GoalStatus.COMPLETED){
            throw new DataValidationException("Цель уже завершена");
        }
        //Содержит только существующие скиллы
        if (!updatedGoal.getSkillsToAchieve().stream()
            .allMatch(skillService::validateSkill)){
             throw new DataValidationException("Некорректные скиллы");
            }


        // Обновить в БД
        updatedGoal.setId(goalId);
        goalRepository.save(updatedGoal);

        //Если цель завершена - присваиваем скиллы всем участникам цели
        if (updatedGoal.getStatus() == GoalStatus.COMPLETED){
            updatedGoal.getUsers().forEach(user -> currentGoal.getSkillsToAchieve()
                            .forEach(skill -> skillService.assignSkillToUser(user.getId(), skill.getId())));
        }


    }
}

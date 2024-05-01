package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_COUNT_ACTIVE_GOALS_PER_USER = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;

    public void createGoal(Long userID, Goal goal) {

    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = findById(goalId);
        //Валидация по статусу
        validateGoalStatus(goalToUpdate);
        //Валидация по существующим скиллам
        validateSkills(goalDto);

        // Обновляем данные цели
        goalToUpdate.setTitle(goalToUpdate.getTitle());

        if (Objects.nonNull(goalDto.getDescription())) {
            goalToUpdate.setDescription(goalDto.getDescription());
        }

        if (Objects.nonNull(goalDto.getParentId())) {
            goalRepository.findById(goalDto.getParentId())
                    .ifPresent(goalToUpdate::setParent);
        }

        if (Objects.nonNull(goalDto.getSkillIds())) {
            List<Skill> skills = goalDto.getSkillIds().stream()
                    .map(skillService::getSkillById)
                    .toList();
            goalToUpdate.setSkillsToAchieve(skills);
        }


        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            List<User> users = goalToUpdate.getUsers();

            //Добавление пользователям навыки цели
            users.forEach((user) -> goalToUpdate.getSkillsToAchieve()
                    .forEach((skill) -> {
                        if (!user.getSkills().contains(skill)) {
                            skillRepository.assignSkillToUser(user.getId(), skill.getId());
                        }
                    }));
        }
        goalRepository.save(goalToUpdate);
        return goalMapper.toDto(goalToUpdate);
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    public Goal findById(Long goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> new EntityNotFoundException("Цель не найдена"));
    }

    public static void validateGoalStatus(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Цель уже завершена");
        }
    }

    private void validateSkills(GoalDto goalDto) {
        goalDto.getSkillIds().forEach((skillId) -> {
            if (!skillRepository.existsById(skillId)) {
                throw new DataValidationException("Навык с id " + skillId + " не существует");
            }
        });
    }
}

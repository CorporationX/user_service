package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillRepository skillRepository;
    private final static int MAX_COUNT_GOALS = 3;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateGoal(userId, goalDto);
        Goal goalEntity = initGoalEntity(goalDto);
        checkSkillsOnExistence(goalEntity);
        goalRepository.create(goalEntity.getTitle(), goalEntity.getDescription(), goalEntity.getParent().getId());
        return goalMapper.toDto(goalRepository.save(goalEntity));
    }

    private void validateGoal(Long userId, GoalDto goalDto) {
        if (goalRepository.countActiveGoalsPerUser(userId) == MAX_COUNT_GOALS) { // Можно создать свой Exception
            throw new DataValidationException("Достигнуто максимальное количество целей у пользователя");
        }
        goalRepository.findGoalsByUserId(userId).forEach(goalFromData -> {
            if(goalDto.getTitle().equalsIgnoreCase(goalFromData.getTitle())){
                throw new DataValidationException("Название такой цели уже есть");
            }
        });
    }

    private void checkSkillsOnExistence(Goal goal) {
        List<Skill> existingSkills = skillRepository.findAll();
        List<Long> idSkillsGoal = goal.getSkillsToAchieve().stream().map(Skill::getId).toList();
        existingSkills.forEach(skill -> {
            if (idSkillsGoal.contains(skill.getId())) {
                throw new DataValidationException("Недопустимые скиллы");
            }
        });
    }

    private Goal initGoalEntity(GoalDto goalDto) {
        Goal goalEntity = goalMapper.toEntity(goalDto);
        List<Long> skillIdGoal = goalDto.getSkillIds();
        List<Skill> goalSkill = skillRepository.findAllById(skillIdGoal);
        goalEntity.setSkillsToAchieve(goalSkill);
        return goalEntity;
    }
}

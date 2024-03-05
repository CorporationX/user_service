package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final static int MAX_COUNT_GOALS = 3;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        Goal goalEntity = initGoalEntity(goalDto);
        validateGoal(userId, goalEntity);
        List<Skill> existingSkills = skillRepository.findAll();
        checkSkillsOnExistence(goalEntity, existingSkills);
        Goal goalToDto = goalRepository.create(goalEntity.getTitle(), goalEntity.getDescription(), goalEntity.getParent().getId());
        assignSkillToGoal(goalEntity);
        assignGoalToUser(userId, goalEntity);
        return goalMapper.toDto(goalToDto);
    }

    private void validateGoal(Long userId, Goal goal) {
        if (goalRepository.countActiveGoalsPerUser(userId) == MAX_COUNT_GOALS) { // Можно создать свой Exception
            throw new RuntimeException("Достигнуто максимальное количество целей у пользователя");
        }
        Optional<Goal> goalOptional = goalRepository.findGoalsByUserId(userId)
                .filter(goalFromData -> goalFromData.getTitle().equalsIgnoreCase(goal.getTitle()))
                .findFirst();
        if (goalOptional.isPresent()) {
            throw new IllegalArgumentException("Название такой цели уже есть");
        }
    }

    private void checkSkillsOnExistence(Goal goal, List<Skill> skills) {
        List<Long> idSkillsGoal = goal.getSkillsToAchieve().stream().map(Skill::getId).toList();
        skills.forEach(skill -> {
            if (idSkillsGoal.contains(skill.getId())) {
                throw new RuntimeException("Недопустимые скиллы");
            }
        });
    }

    private void assignSkillToGoal(Goal goal) {
        goal.getSkillsToAchieve()
                .forEach(skill -> skillRepository.assignSkillToGoal(skill.getId(), goal.getId()));
    }

    private void assignGoalToUser(Long userId, Goal goal) {
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(value -> {
            value.getGoals().add(goal);
            goalRepository.assignGoalToUser(userId, goal.getId());
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

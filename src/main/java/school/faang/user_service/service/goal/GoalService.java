package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final int MAX_ACTIVE_GOALS = 3;

    public Goal createGoal(Long userId, Goal goal) {
        creatingGoalValidation(userId, goal);
        GoalDto dto = goalMapper.toDto(goal);
        //goalMapper.toDto(goal).getSkillIds().forEach(id -> goalRepository.addSkillToGoal(id, goal.getId())); По заданию этот метод должен быть и мне его делать не нужно
        return goalRepository.save(goal);
    }

    private void creatingGoalValidation(Long userId, Goal goal) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_ACTIVE_GOALS) {
            throw new DataValidationException("Out of MAX_ACTIVE_GOALS range");
        }
        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        skillsToAchieve.forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new DataValidationException("Contains a non-existence skill");
            }
        });
    }
}

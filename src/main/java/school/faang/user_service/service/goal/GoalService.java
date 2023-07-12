package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper = GoalMapper.INSTANCE;
    private final int MAX_GOALS_PER_USER = 3;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .filter(goal -> {
                    if (filterDto.getGoalStatus() == null) {
                        return true;
                    }
                    return goal.getStatus().equals(filterDto.getGoalStatus());
                })
                .filter(goal -> {
                    if (filterDto.getSkillId() == null) {
                        return true;
                    }
                    return goalContainSkillId(filterDto, goal);
                })
                .filter(goal -> {
                    if (filterDto.getTitle() == null) {
                        return true;
                    }
                    return goal.getTitle().equals(filterDto.getTitle());
                })
                .toList();

        return goalMapper.toDtoList(goals);
    }

    private boolean goalContainSkillId(GoalFilterDto filterDto, Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList()
                .contains(filterDto.getSkillId());
    }

    public Goal createGoal(Long userId, Goal goal) {
        validateGoalToCreate(userId, goal);
        return goalRepository.save(goal);
    }

    private void validateGoalToCreate(Long userId, Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (goal.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS_PER_USER) {
            throw new IllegalArgumentException("Maximum number of goals for this user reached");
        }
        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        skillsToAchieve.forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new IllegalArgumentException("Skill not found");
            }
        });
    }
}

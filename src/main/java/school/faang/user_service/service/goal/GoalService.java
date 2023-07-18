package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final int MAX_ACTIVE_GOALS = 3;

    public void createGoalValidation(Long userId, Goal goal) {
        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoalsCount >= MAX_ACTIVE_GOALS ) {
            throw new IllegalArgumentException("Goal cannot be saved because MAX_ACTIVE_GOALS = "
                    + MAX_ACTIVE_GOALS + " and current active goals = "
                    + activeGoalsCount);
        }
        GoalDto goalDto = goalMapper.toDto(goal);
        if (skillRepository.countExisting(goalDto.getSkillIds()) != goalDto.getSkillIds().size()) {
            throw new IllegalArgumentException("Goal contains non-existent skill");
        }
    }

    public void createGoal(Long userId, Goal goal) {
        createGoalValidation(userId, goal);
        goalRepository.save(goal);
        GoalDto dto = goalMapper.toDto(goal);
        Long gid = dto.getId();
        dto.getSkillIds().stream().forEach(sid -> goalRepository.connectGoalWithSkill(gid, sid));
    }
}

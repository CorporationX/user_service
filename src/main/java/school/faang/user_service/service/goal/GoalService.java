package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillRepository skillRepository;
    private static final int MAX_USER_ACTIVE_GOALS = 3;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoal(userId, goalDto, MAX_USER_ACTIVE_GOALS);

        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        assignGoalToUser(userId, createdGoal.getId());
        assignSkillsToGoal(createdGoal.getId(), goalDto.getSkillIds());

        return goalMapper.toDto(createdGoal);
    }

    private void assignGoalToUser(Long userId, Long goalId) {
        goalRepository.assignGoalToUser(goalId, userId);
    }

    private void assignSkillsToGoal(Long goalId, List<Long> skillIds) {
        skillIds.forEach(skillId -> skillRepository.assignSkillToGoal(skillId, goalId));
    }
}

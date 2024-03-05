package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
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
        goalValidator.validateGoalCreation(userId, goalDto, MAX_USER_ACTIVE_GOALS);

        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        createdGoal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
        goalRepository.assignGoalToUser(createdGoal.getId(), userId);

        return goalMapper.toDto(goalRepository.save(createdGoal));
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidator.validateGoalUpdate(goalId, goalDto);

        Goal goal = goalRepository.findById(goalId).get();
        updateGoalFields(goal, goalDto);

        return goalMapper.toDto(goalRepository.save(goal));
    }

    private void updateGoalFields(Goal goal, GoalDto goalDto) {
        if (goalDto.getParentId() != null) {
            Long parentId = goalDto.getParentId();
            goalValidator.validateGoalExists(parentId);
            goal.setParent(goalRepository.findById(parentId).get());
        }
        goal.setTitle(goalDto.getTitle());
        goal.setStatus(goalDto.getStatus());
        goal.setDescription(goalDto.getDescription());
        goal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
    }

    private List<Skill> skillIdsToSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}

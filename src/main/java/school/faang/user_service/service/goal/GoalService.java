package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoalCreation(userId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        setUpGoalFields(goal, goalDto, userId);
        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidator.validateGoalUpdate(goalId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalValidator.validateNull(goalId);
        goalRepository.deleteById(goalId);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters) {
        goalValidator.validateGoalExists(goalId);
        List<Goal> goals = goalRepository.findByParent(goalId)
                .collect(Collectors.toList());
        applyFilters(goals, filters);
        return goalMapper.toDto(goals);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .collect(Collectors.toList());
        applyFilters(goals, filters);
        return goalMapper.toDto(goals);
    }

    private void applyFilters(List<Goal> goals, GoalFilterDto filters) {
        if (!goalFilters.isEmpty()) {
            goalFilters.stream()
                    .filter(goalFilter -> goalFilter.isApplicable(filters))
                    .forEach(goalFilter -> goalFilter.apply(goals, filters));
        }
    }

    private void setUpGoalFields(Goal goal, GoalDto goalDto, long userId) {
        User user = goalValidator.validateOptional(userRepository.findById(userId), String.format("User with ID %d not found", userId));
        Long parentId = goalDto.getParentId();
        if (parentId != null) {
            Goal parent = goalValidator.validateOptional(goalRepository.findById(parentId), String.format("Parent goal with ID %d not found", parentId));
            goal.setParent(parent);
        }
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
        goal.setUsers(List.of(user));
    }

    private List<Skill> skillIdsToSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}

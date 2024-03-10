package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.validation.goal.GoalValidation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillRepository skillRepository;
    private final GoalValidation goalValidation;
    private final UserRepository userRepository;
    private final List<GoalFilter> filters;
    private final static int MAX_COUNT_GOALS = 3;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidation.validateGoalCreate(userId, goalDto, MAX_COUNT_GOALS);
        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        createdGoal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
        createdGoal.setUsers(Arrays.asList(getUser(userId)));
        return goalMapper.toDto(goalRepository.save(createdGoal));
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidation.validateGoalUpdate(goalId, goalDto);
        Goal createdGoal = goalRepository.findById(goalId).get();
        updateFields(createdGoal, goalDto);
        return goalMapper.toDto(goalRepository.save(createdGoal));
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalValidation.validateExistGoal(goalId);
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filterGoalDto) {
        goalValidation.validateExistGoal(goalId);
        List<Goal> goals = goalRepository.findByParent(goalId).collect(Collectors.toList());
        applyFilters(goals, filterGoalDto);
        return goalsToDto(goals);
    }

    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filterGoalDto) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).collect(Collectors.toList());
        applyFilters(goals, filterGoalDto);
        return goalsToDto(goals);
    }

    private User getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataValidationException("User doesnt exist in database");
        }
        return user.get();
    }

    private void updateFields(Goal goal, GoalDto goalDto) {
        if (goalDto.getParentId() != null) {
            Long parentId = goalDto.getParentId();
            goalValidation.validateExistGoal(parentId);
            goal.setParent(goalRepository.findById(parentId).get());
        }
        if (goalDto.getStatus().equals(GoalStatus.COMPLETED)) {
            goal.getUsers().forEach(user -> addSkillsToUser(user, goal));
        }
        goal.setTitle(goalDto.getTitle());
        goal.setStatus(goalDto.getStatus());
        goal.setDescription(goalDto.getDescription());
        goal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
    }

    private void addSkillsToUser(User user, Goal goal) {
        goal.getSkillsToAchieve().forEach(skill -> {
            if (!user.getSkills().contains(skill)) {
                skillRepository.assignSkillToUser(skill.getId(), user.getId());
            }
        });
    }

    private List<Skill> skillIdsToSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    private void applyFilters(List<Goal> goals, GoalFilterDto filterGoalDto) {
        filters.stream()
                .filter(filter -> filter.isApplicable(filterGoalDto))
                .forEach(filter -> filter.apply(goals, filterGoalDto));
    }

    private List<GoalDto> goalsToDto(List<Goal> goals) {
        return goals.stream()
                .map(goalMapper::toDto)
                .toList();
    }
}

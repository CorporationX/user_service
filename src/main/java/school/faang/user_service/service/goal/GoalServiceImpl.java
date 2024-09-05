package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapping.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@Service
@Transactional
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    @Override
    public void createGoal(GoalDto goalDto) {
        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentGoalId());

        goalRepository.assignGoalToUser(createdGoal.getId(), goalDto.getUserId());
        assignSkillsToGoal(goalDto, createdGoal.getId());
    }

    @Override
    public void updateGoal(GoalDto goalDto) {
        Goal goalFromDb = findById(goalDto.getGoalId());

        goalRepository.update(goalDto.getGoalId(), goalDto.getTitle(), goalDto.getDescription(),
            goalDto.getParentGoalId(), goalDto.getStatus().ordinal());

        updateSkills(goalDto);

        if (isMakeGoalCompleted(goalDto, goalFromDb)) {
            assignGoalSkillsToUsers(goalFromDb);
        }
    }

    @Override
    public void deleteGoal(Long goalId) {
        List<Long> goalIds = new ArrayList<>();
        goalRepository.findByParent(goalId)
            .toList()
            .forEach(goal -> goalIds.add(goal.getId()));
        goalIds.add(goalId);

        for (Long id : goalIds) {
            goalRepository.unassignGoalFromUsers(id);
            skillRepository.findSkillsByGoalId(id)
                .forEach(skill -> skillRepository.unassignSkillFromUsers(skill.getId()));
            skillRepository.deleteSkillsByGoalId(id);

            goalRepository.delete(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalDto> findSubGoalsByParentGoalId(Long parentGoalId, GoalFilterDto filterDto) {
        Predicate<Goal> predicate = combinePredicateFromFilter(filterDto);
        List<Goal> filteredGoals = filterGoalsByPredicate(goalRepository.findByParent(parentGoalId), predicate);
        return goalMapper.toDto(filteredGoals);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filterDto) {
        Predicate<Goal> predicate = combinePredicateFromFilter(filterDto);
        List<Goal> goals = filterGoalsByPredicate(goalRepository.findGoalsByUserId(userId), predicate);
        return goalMapper.toDto(goals);
    }

    private Goal findById(Long goalId) {
        return goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));
    }

    private void updateSkills(GoalDto goalDto) {
        skillRepository.deleteSkillsByGoalId(goalDto.getGoalId());
        assignSkillsToGoal(goalDto, goalDto.getGoalId());
    }

    private void assignSkillsToGoal(GoalDto goalDto, Long goalId) {
        List<Long> skillIds = goalDto.getSkillIds();
        if (isNotEmpty(skillIds)) {
            skillIds.forEach(skillId -> skillRepository.addSkillToGoal(skillId, goalId));
        }
    }

    private void assignGoalSkillsToUsers(Goal goal) {
        List<Skill> skills = skillRepository.findSkillsByGoalId(goal.getId());
        List<User> users = goal.getUsers();
        for (User user : users) {
            for (Skill skill : skills) {
                skillRepository.assignSkillToUser(skill.getId(), user.getId());
            }
        }
    }

    private boolean isMakeGoalCompleted(GoalDto dto, Goal goal) {
        return dto.getStatus() == COMPLETED && goal.getStatus() == ACTIVE;
    }

    private Predicate<Goal> combinePredicateFromFilter(GoalFilterDto filterDto) {
        return goalFilters.stream()
            .filter(filter -> filter.isApplicable(filterDto))
            .map(filter -> filter.apply(filterDto))
            .reduce(goal -> true, Predicate::and);
    }

    private List<Goal> filterGoalsByPredicate(Stream<Goal> goals, Predicate<Goal> predicate) {
        return goals
            .filter(predicate)
            .toList();
    }
}

package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.ResourceNotFoundException;
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
    private final List<GoalFilter> goalFilters;

    @Override
    public void createGoal(Goal goal, Long userId) {
        Goal createdGoal = goalRepository.create(
            goal.getTitle(), goal.getDescription(), goal.getParent() == null ? null : goal.getParent().getId());

        goalRepository.assignGoalToUser(createdGoal.getId(), userId);
        assignSkillsToGoal(goal, createdGoal.getId());
    }

    @Override
    public void updateGoal(Goal goal) {
        goalRepository.update(goal.getId(), goal.getTitle(), goal.getDescription(),
            goal.getParent() == null ? null : goal.getParent().getId(), goal.getStatus().ordinal());

        updateSkills(goal);

        if (isMakeGoalCompleted(goal)) {
            assignGoalSkillsToUsers(goal);
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
    public List<Goal> findSubGoalsByParentGoalId(Long parentGoalId, GoalFilterDto filterDto) {
        Predicate<Goal> predicate = combinePredicateFromFilter(filterDto);
        return filterGoalsByPredicate(goalRepository.findByParent(parentGoalId), predicate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goal> findGoalsByUserId(Long userId, GoalFilterDto filterDto) {
        Predicate<Goal> predicate = combinePredicateFromFilter(filterDto);
        return filterGoalsByPredicate(goalRepository.findGoalsByUserId(userId), predicate);
    }

    private Goal findById(Long goalId) {
        return goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));
    }

    private void updateSkills(Goal goal) {
        skillRepository.deleteSkillsByGoalId(goal.getId());
        assignSkillsToGoal(goal, goal.getId());
    }

    private void assignSkillsToGoal(Goal goal, Long goalId) {
        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        if (isNotEmpty(skillsToAchieve)) {
            List<Long> skillIds = skillsToAchieve.stream()
                .map(Skill::getId)
                .toList();
            skillIds.forEach(skillId -> skillRepository.addSkillToGoal(skillId, goalId));
        }
    }

    private void assignGoalSkillsToUsers(Goal goal) {
        List<Skill> skills = skillRepository.findSkillsByGoalId(goal.getId());
        Goal foundGoal = goalRepository.findById(goal.getId()).get();
        foundGoal.getUsers().forEach(user -> {
            for (Skill skill : skills) {
                skillRepository.assignSkillToUser(skill.getId(), user.getId());
            }
        });
    }

    private boolean isMakeGoalCompleted(Goal goal) {
        Goal goalFromDb = findById(goal.getId());
        return goal.getStatus() == COMPLETED && goalFromDb.getStatus() == ACTIVE;
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

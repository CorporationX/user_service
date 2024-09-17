package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    @Value("${app.goal.max-active-per-user}")
    private Integer maxExistedActiveGoals;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final List<GoalFilter> goalFilters;

    @Override
    @Transactional
    public Goal createGoal(Goal goal, Long userId, Long parentGoalId, List<Long> skillIds) {
        validateExistingSkills(skillIds);

        int alreadyExistedActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (alreadyExistedActiveGoals >= maxExistedActiveGoals) {
            throw new BadRequestException("User %s can have maximum %s goals", userId, maxExistedActiveGoals);
        }

        if (parentGoalId != null) {
            Goal parentGoal = findById(parentGoalId, "Parent Goal");
            goal.setParent(parentGoal);
        }

        User user = userRepository.findById(userId).
            orElseThrow(() -> new ResourceNotFoundException("User", userId));
        goal.setUsers(List.of(user));

        if (skillIds != null) {
            List<Skill> skills = skillRepository.findByIds(skillIds);
            goal.setSkillsToAchieve(skills);
        }

        return goalRepository.save(goal);
    }

    @Override
    @Transactional
    public Goal updateGoal(Goal goal, List<Long> skillIds) {
        validateExistingSkills(skillIds);

        Goal existedGoal = findById(goal.getId());

        if (existedGoal.getStatus() == COMPLETED) {
            throw new BadRequestException("You cannot update Goal %s with the %s status.", existedGoal.getId(), COMPLETED.name());
        }
        boolean isMakeGoalCompleted = goal.getStatus() == COMPLETED && existedGoal.getStatus() == ACTIVE;

        existedGoal.setTitle(goal.getTitle());
        existedGoal.setDescription(goal.getDescription());
        existedGoal.setStatus(goal.getStatus());

        if (skillIds != null) {
            List<Skill> skills = skillRepository.findByIds(skillIds);
            existedGoal.setSkillsToAchieve(skills);
        }

        if (isMakeGoalCompleted) {
            List<Skill> skills = existedGoal.getSkillsToAchieve();
            if (isEmpty(skills)) {
                throw new BadRequestException("You cannot complete Goal %s with empty list of Goals.", existedGoal.getId());
            }
            List<User> users = existedGoal.getUsers();
            skills.forEach(skill -> {
                users.forEach(user -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));
            });
        }

        return goalRepository.save(existedGoal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long goalId) {
        goalRepository.findGoalsByParent(goalId)
            .forEach(goal -> goalRepository.deleteById(goal.getId()));

        goalRepository.deleteById(goalId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goal> findSubGoalsByParentGoalId(Long parentGoalId, GoalFilterDto filterDto) {
        Predicate<Goal> predicate = combinePredicateFromFilter(filterDto);
        List<Goal> subGoalsByParentGoal = goalRepository.findGoalsByParent(parentGoalId);
        Stream<Goal> subGoalsByParentGoalStream = subGoalsByParentGoal.stream();
        return filterGoalsByPredicate(subGoalsByParentGoalStream, predicate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goal> findGoalsByUserId(Long userId, GoalFilterDto filterDto) {
        Predicate<Goal> predicate = combinePredicateFromFilter(filterDto);
        List<Goal> goalsByUser = goalRepository.findByUserId(userId);
        Stream<Goal> goalsByUserStream = goalRepository.findGoalsByUserId(userId);
        return filterGoalsByPredicate(goalsByUserStream, predicate);
    }

    public Goal findById(Long goalId) {
        return findById(goalId, "Goal");
    }

    public Goal findById(Long goalId, String field) {
        return goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException(field, goalId));
    }

    @Transactional
    public void deleteGoalAndUnlinkChildren(Goal goal) {
        goal.getChildrenGoals()
                .forEach(child -> {
                    child.setParent(null);
                    goalRepository.save(child);
                });

        goalRepository.delete(goal);
    }

    private void validateExistingSkills(List<Long> skillIds) {
        if (skillIds != null && skillIds.size() != skillRepository.countExisting(skillIds)) {
            throw new BadRequestException("Skills from request are not presented in DB");
        }
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

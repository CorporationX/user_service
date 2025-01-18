package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;

    @Value("${goal.max-active-goals-per-user}")
    private Integer maxActiveGoalsPerUser;

    @Transactional
    public Goal createGoal(Long userId, String title, String description, Long parentId, List<Long> skillIds) {
        //validation
        int numOfActiveGoals = goalRepository.countActiveGoalsPerUser(userId);

        if (numOfActiveGoals >= maxActiveGoalsPerUser) {
            throw new IllegalStateException(String.format("User with id %s has %s or more active goals",
                    userId, maxActiveGoalsPerUser));
        }

        validateSkills(skillIds);

        //perform goal creation
        Goal createdGoal = goalRepository.create(title, description, parentId);
        assignSkillsToGoal(createdGoal.getId(), skillIds);

        return createdGoal;
    }

    @Transactional
    public Goal updateGoal(Long goalId, Goal goalUpdated, List<Long> skillIds) {
        //validation
        Goal goalOld = goalRepository.findById(goalId).orElseThrow(() -> new NoSuchElementException(String.format("No goal found with such id %s", goalId)));

        if (Objects.equals(goalOld.getStatus(), GoalStatus.COMPLETED)) {
            throw new IllegalStateException(String.format("The goal with id %s and title %s is already completed and impossible to modify", goalId, goalOld.getTitle()));
        }

        validateSkills(skillIds);

        //perform goal update
        setAllMissingFields(goalUpdated, goalOld);
        goalUpdated = goalRepository.save(goalUpdated);

        //update skills assigned to the goal
        goalRepository.removeSkillsFromGoal(goalId);
        assignSkillsToGoal(goalId, skillIds);

        if (Objects.equals(goalUpdated.getStatus(), GoalStatus.COMPLETED)) {
            skillService.assignSkillsFromGoalToUsers(goalId, goalUpdated.getUsers());
        }

        return goalUpdated;
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional(readOnly = true)
    public List<Goal> findSubGoalsByParentId(Long parentGoalId, GoalFilterDto filterDto) {
        Stream<Goal> subGoals = goalRepository.findByParent(parentGoalId);
        List<GoalFilter> applicableFilters = goalFilters.stream().filter(goalFilter -> goalFilter.isApplicable(filterDto)).toList();

        return subGoals.filter(goal ->
                        applicableFilters.stream().allMatch(filter -> filter.apply(filterDto, goal))
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Goal> findSubGoalsByUserId(Long userId, GoalFilterDto filterDto) {
        Stream<Goal> subGoals = goalRepository.findGoalsByUserId(userId);
        List<GoalFilter> applicableFilters = goalFilters.stream().filter(goalFilter -> goalFilter.isApplicable(filterDto)).toList();

        return subGoals.filter(goal ->
                        applicableFilters.stream().allMatch(filter -> filter.apply(filterDto, goal))
                )
                .toList();
    }

    private void setAllMissingFields(Goal goalTo, Goal goalFrom) {
        goalTo.setId(goalTo.getId() == null ? goalFrom.getId() : goalTo.getId());
        goalTo.setParent(goalTo.getParent() == null ? goalFrom.getParent() : goalTo.getParent());
        goalTo.setTitle(goalTo.getTitle() == null ? goalFrom.getTitle() : goalTo.getTitle());
        goalTo.setDescription(goalTo.getDescription() == null ? goalFrom.getDescription() : goalTo.getDescription());
        goalTo.setStatus(goalTo.getStatus() == null ? goalFrom.getStatus() : goalTo.getStatus());
        goalTo.setDeadline(goalTo.getDeadline() == null ? goalFrom.getDeadline() : goalTo.getDeadline());
        goalTo.setCreatedAt(goalTo.getCreatedAt() == null ? goalFrom.getCreatedAt() : goalTo.getCreatedAt());
        goalTo.setMentor(goalTo.getMentor() == null ? goalFrom.getMentor() : goalTo.getMentor());
        goalTo.setInvitations(goalTo.getInvitations() == null ? goalFrom.getInvitations() : goalTo.getInvitations());
        goalTo.setUsers(goalTo.getUsers() == null ? goalFrom.getUsers() : goalTo.getUsers());
        goalTo.setSkillsToAchieve(goalTo.getSkillsToAchieve() == null ? goalFrom.getSkillsToAchieve() : goalTo.getSkillsToAchieve());
    }

    @Transactional
    private void assignSkillsToGoal(Long goalId, List<Long> skillsId) {
        if (skillsId == null) {
            return;
        }

        skillsId.forEach(skillId -> goalRepository.addSkillToGoalById(goalId, skillId));
    }

    private void validateSkills(List<Long> skillsId) {
        if (skillsId == null) {
            return;
        }

        skillsId.forEach(id -> skillService.findSkillById(id).orElseThrow(
                () -> new NoSuchElementException(String.format("Skill with id %s doesn't exist", id))
        ));
    }
}

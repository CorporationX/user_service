package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final List<GoalFilter> goalFilters;

    @Value("${goal.max-active-goals-per-user}")
    private Integer maxActiveGoalsPerUser;

    @Transactional
    public Goal createGoal(Long userId, String title, String description, Long parentId, List<Long> skillIds) {
        //validation
        if (!userService.userExists(userId)) {
            log.error("User with id {} doesn't exist", userId);
            throw new NoSuchElementException(String.format("User with id %s doesn't exist", userId));
        }

        int numOfActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (numOfActiveGoals >= maxActiveGoalsPerUser) {
            log.error("User with id {} has {} or more active goals", userId, maxActiveGoalsPerUser);
            throw new IllegalStateException(String.format("User with id %s has %s or more active goals",
                    userId, maxActiveGoalsPerUser));
        }

        validateSkills(skillIds);

        //perform goal creation
        Goal createdGoal = goalRepository.create(title, description, parentId);
        assignSkillsToGoal(createdGoal.getId(), skillIds);

        log.info("Goal with id {} and title {} has been created successfully and skills {} have been assigned", createdGoal.getId(), createdGoal.getTitle(), skillIds);
        return createdGoal;
    }

    @Transactional
    public Goal updateGoal(Long goalId, Goal goalUpdated, Long goalParentId, List<Long> skillIds) {
        //validation
        Goal goalOld = goalRepository.findById(goalId).orElseThrow(() -> new NoSuchElementException(String.format("No goal found with such id %s", goalId)));
        if (goalOld.getStatus() == GoalStatus.COMPLETED) {
            log.error("The goal with id {} and title {} is already completed and impossible to modify", goalId, goalOld.getTitle());
            throw new IllegalStateException(String.format("The goal with id %s and title %s is already completed and impossible to modify", goalId, goalOld.getTitle()));
        }

        validateSkills(skillIds);

        //perform goal update
        if (goalParentId != null) {
            goalUpdated.setParent(Goal.builder().id(goalParentId).build());
        }
        setAllMissingFields(goalUpdated, goalOld);
        goalUpdated = goalRepository.save(goalUpdated);
        log.info("Goal with id {} and title {} has been updated successfully", goalUpdated.getId(), goalUpdated.getTitle());

        //update skills assigned to the goal
        if (!skillIds.isEmpty()) {
            goalRepository.removeSkillsFromGoal(goalId);
            assignSkillsToGoal(goalId, skillIds);
            log.info("Skills with ids {} have been set for the goal with id {}", skillIds, goalId);
        }

        if (goalUpdated.getStatus() == GoalStatus.COMPLETED) {
            skillService.assignSkillsFromGoalToUsers(goalId, goalUpdated.getUsers());
            log.info("Skills from the goal with id {} have been assigned to the users {}", goalId, goalUpdated.getUsers().stream().map(User::getId).toList());
        }

        return goalUpdated;
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional(readOnly = true)
    public List<Goal> findSubGoalsByParentId(Long parentGoalId, GoalFilterDto filterDto) {
        Stream<Goal> subGoals = goalRepository.findByParent(parentGoalId);
        List<GoalFilter> applicableFilters = goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filterDto))
                .toList();

        return subGoals.filter(goal -> applicableFilters.stream()
                                .allMatch(filter -> filter.apply(filterDto, goal))
                ).toList();
    }

    @Transactional(readOnly = true)
    public List<Goal> findSubGoalsByUserId(Long userId, GoalFilterDto filterDto) {
        //validation
        if (!userService.userExists(userId)) {
            log.error("User with id {} doesn't exist", userId);
            throw new NoSuchElementException(String.format("User with id %s doesn't exist", userId));
        }

        //perform retrieval and filtration
        Stream<Goal> subGoals = goalRepository.findGoalsByUserId(userId);
        List<GoalFilter> applicableFilters = goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filterDto))
                .toList();

        return subGoals.filter(goal -> applicableFilters.stream()
                                .allMatch(filter -> filter.apply(filterDto, goal))
                ).toList();
    }

    private void setAllMissingFields(Goal goalTo, Goal goalFrom) {
        goalTo.setId((Long) getOrDefault(goalTo.getId(), goalFrom.getId()));
        goalTo.setParent((Goal) getOrDefault(goalTo.getParent(), goalFrom.getParent()));
        goalTo.setTitle((String) getOrDefault(goalTo.getTitle(), goalFrom.getTitle()));
        goalTo.setDescription((String) getOrDefault(goalTo.getDescription(), goalFrom.getDescription()));
        goalTo.setStatus((GoalStatus) getOrDefault(goalTo.getStatus(), goalFrom.getStatus()));
        goalTo.setDeadline((LocalDateTime) getOrDefault(goalTo.getDeadline(), goalFrom.getDeadline()));
        goalTo.setCreatedAt((LocalDateTime) getOrDefault(goalTo.getCreatedAt(), goalFrom.getCreatedAt()));
        goalTo.setMentor((User) getOrDefault(goalTo.getMentor(), goalFrom.getMentor()));
        goalTo.setInvitations((List<GoalInvitation>) getOrDefault(goalTo.getInvitations(), goalFrom.getInvitations()));
        goalTo.setUsers((List<User>) getOrDefault(goalTo.getUsers(), goalFrom.getUsers()));
        goalTo.setSkillsToAchieve((List<Skill>) getOrDefault(goalTo.getSkillsToAchieve(), goalFrom.getSkillsToAchieve()));
    }

    private Object getOrDefault(Object value, Object defaultValue) {
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    private void assignSkillsToGoal(Long goalId, @NotNull(message = "list of skills can't be null") List<Long> skillsId) {
        skillsId.forEach(skillId -> goalRepository.addSkillToGoalById(goalId, skillId));
    }

    private void validateSkills(@NotNull(message = "list of skills can't be null") List<Long> skillsId) {
        skillsId.forEach(id -> skillService.findSkillById(id).orElseThrow(
                () -> new NoSuchElementException(String.format("Skill with id %s doesn't exist", id))
        ));
    }
}

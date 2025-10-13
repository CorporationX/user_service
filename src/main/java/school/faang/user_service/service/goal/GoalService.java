package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.goal.FilterGoal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.goal.validator.GoalValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final List<FilterGoal> filterGoals;

    public Goal create(Goal goal, List<Long> userIds,
                       List<Long> skillIds, Long mentorId) {

        GoalValidator.validateMentorOrUsersPresence(userIds, mentorId);

        GoalValidator.validateUserAccess(userIds, mentorId, userContext.getUserId());

        if (userIds != null) {
            List<User> userList = userRepository.findAllById(userIds);
            GoalValidator.validateUserGoalsLimit(userList);
            goal.setUsers(userList);
        }
        if (mentorId != null) {
            User mentor = userRepository.getByIdOrThrow(mentorId);
            goal.setMentor(mentor);
        }
        if (skillIds != null) {
            List<Skill> skillList = skillRepository.findAllById(skillIds);
            goal.setSkillsToAchieve(skillList);
        }

        goal.setStatus(ACTIVE);
        goalRepository.save(goal);
        log.info("The goal has been added to the database. id - {}, tittle - {}", goal.getId(), goal.getTitle());
        return goal;
    }

    @Transactional
    public Goal update(Long goalId, GoalUpdateDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);

        long oldMentorId = goal.getMentor().getId();

        GoalValidator.validateUserAccessToGoal(oldMentorId, goal, userContext.getUserId());

        updateGoal(goal, updateGoalDto);

        if (Objects.equals(goal.getSkillsToAchieve(), COMPLETED)) {
            completionOfTheGoal(goal);
        }

        goalRepository.save(goal);
        log.info("the goal id - {}, tittle - {} was changed", goal.getId(), goal.getTitle());
        return goal;
    }

    public void delete(long goalId) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        Long mentorId = goal.getMentor().getId();
        Long userId = userContext.getUserId();

        GoalValidator.validateUserAccessToGoal(mentorId, goal, userId);

        if (Objects.equals(mentorId, userId) && Objects.nonNull(mentorId)) {
            goalRepository.deleteById(goalId);
            log.info("The user with {} has been removed from the goal id-{}'s participants", userId, goalId);
        } else {
            removeUserFromGoal(userId, goal);
        }
    }

    public List<Goal> getByFilters(GoalFilterDto filters) {
        Stream<Goal> goalsStream = goalRepository.findAll().stream();

        for (FilterGoal filterGoal : filterGoals) {
            if (filterGoal.isApplication(filters)) {
                goalsStream = filterGoal.apply(goalsStream, filters);
            }
        }
        List<Goal> goals = goalsStream.toList();
        if (goals.isEmpty()) {
            throw new ForbiddenException("The list after filtering does not contain any targets");
        }
        log.info("A list of goals for the specified filter has been created.");
        return goals;
    }

    private void removeUserFromGoal(Long userId, Goal goal) {
        Long goalId = goal.getId();
        if (goal.getUsers().size() == 1) {
            goalRepository.deleteById(goalId);
            log.info("the goal id - {}, tittle - {} was deleted", goalId, goal.getTitle());
        } else {
            goalRepository.deleteUserFromGoal(userId, goalId);
            log.info("The user with {} has been removed from the goal id-{}'s participants", userId, goalId);
        }
    }

    private void updateGoal(Goal goal, GoalUpdateDto updateGoalDto) {
        if (Objects.nonNull(updateGoalDto.title())) {
            goal.setTitle(updateGoalDto.title());
        }

        if (Objects.nonNull(updateGoalDto.description())) {
            goal.setDescription(updateGoalDto.description());
        }

        if (Objects.nonNull(updateGoalDto.deadline())) {
            goal.setDeadline(updateGoalDto.deadline());
        }

        if (Objects.nonNull(updateGoalDto.skillIds())) {
            List<Skill> skillList = skillRepository.findAllById(updateGoalDto.skillIds());
            goal.setSkillsToAchieve(skillList);
        }

        if (updateGoalDto.mentorId() != null) {
            User mentor = userRepository.getByIdOrThrow(updateGoalDto.mentorId());
            goal.setMentor(mentor);
        }

        GoalStatus goalStatus = updateGoalDto.status();
        if (Objects.nonNull(goalStatus)) {
            GoalValidator.validateGoalStatusTransition(goal, goalStatus, userContext.getUserId());
            goal.setStatus(goalStatus);
        }
    }

    private void completionOfTheGoal(Goal goal) {
        Set<Long> skillsToAssign = new HashSet<>(goal.getSkillsToAchieve()).stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
        goal.getUsers().forEach(user -> {
            skillsToAssign
                    .forEach(skillId -> skillRepository.assignSkillToUser(skillId, user.getId()));
        });
    }
}

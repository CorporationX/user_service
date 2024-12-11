package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.specification.GoalSpecification;
import school.faang.user_service.util.CollectionUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.logging.goal.GoalMessages.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class GoalService {
    private static final int MAX_NUM_ACTIVE_GOALS = 3;

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserService userService;
    private final SkillService skillService;

    @Transactional
    public GoalResponseDto create(CreateGoalDto createGoalDto) {
        log.info("Creating new goal with title: {}", createGoalDto.title());
        Goal transientGoal = createInitialGoal(createGoalDto);
        establishAllRelations(transientGoal, createGoalDto);
        validateMaxActiveGoalsLimit(transientGoal);

        Goal persistantGoal = goalRepository.save(transientGoal);
        log.info("Successfully created goal with id: {}", persistantGoal.getId());
        return goalMapper.toResponseDto(persistantGoal);
    }

    public Goal getGoalById(long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal do not found"));
    }

    public void removeGoalsWithoutExecutingUsers(List<Goal> goals) {
        goals.stream()
                .filter(Goal::isEmptyExecutingUsers)
                .forEach(goal -> goalRepository.deleteById(goal.getId()));
        log.info("Goals without users is removed");
    }

    private Goal createInitialGoal(CreateGoalDto dto) {
        Goal goal = goalMapper.toEntity(dto);
        goal.setStatus(GoalStatus.ACTIVE);
        return goal;
    }


    public List<Goal> mapListIdsToGoals(List<Long> goalsIds) {
        return goalsIds.stream()
                .map(this::getGoalById)
                .toList();
    }


    private void establishAllRelations(Goal goal, CreateGoalDto dto) {
        log.debug("Establishing relations for goal with title: {}", goal.getTitle());
        setParentGoalIfProvided(goal, dto.parentId());
        setMentorIfProvided(goal, dto.mentorId());

        List<User> users = userService.getAllUsersByIds(dto.usersId());
        if (users.isEmpty()) {
            log.warn("No users found for provided IDs: {}", dto.usersId());
            throw new ResourceNotFoundException("User", "id", dto.usersId());
        }
        goal.setUsers(users);
        users.forEach(user -> user.addGoal(goal));

        List<Skill> skills = skillService.getAllSkillsByIds(dto.skillsToAchieveIds());
        if (skills.isEmpty()) {
            log.warn(NO_SKILLS_FOUND, dto.skillsToAchieveIds());
            throw new ResourceNotFoundException("Skill", "id", dto.skillsToAchieveIds());
        }
        goal.setSkillsToAchieve(skills);
        skills.forEach(skill -> skill.addGoal(goal));
    }

    @Transactional
    public void delete(long goalId) {
        log.info("Deleting goal with id: {}", goalId);
        deleteGoalWithChildren(goalId);
        log.info(SUCCESSFULLY_DELETED_GOAL_AND_ALL_ITS_CHILDREN, goalId);
    }

    private void deleteGoalWithChildren(Long goalId) {
        Deque<Goal> goalsToDelete = new ArrayDeque<>();
        Goal rootGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.warn(GOAL_NOT_FOUND, goalId);
                    return new ResourceNotFoundException("Goal", "id", goalId);
                });
        goalsToDelete.push(rootGoal);

        while (!goalsToDelete.isEmpty()) {
            Goal currentGoal = goalsToDelete.peek();
            List<Goal> children = goalRepository.findAllByParentId(currentGoal.getId());

            if (children.isEmpty()) {
                Goal goalToDelete = goalsToDelete.pop();
                log.debug("Deleting goal with id: {}", goalToDelete.getId());
                removeGoalReferences(goalToDelete);
                goalRepository.delete(goalToDelete);
            } else {
                goalsToDelete.addAll(children);
            }
        }
        log.info(SUCCESSFULLY_DELETED_GOAL_AND_ALL_ITS_CHILDREN, goalId);
    }

    private void removeGoalReferences(Goal goal) {
        goal.getSkillsToAchieve().forEach(skill -> skill.removeGoal(goal));
        goal.getUsers().forEach(user -> user.removeGoal(goal));
    }

    @Transactional
    public GoalResponseDto update(Long goalId, UpdateGoalDto updateGoalDto) {
        log.info("Updating goal with id: {}", goalId);
        Goal persistanceGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.warn(GOAL_NOT_FOUND, goalId);
                    return new ResourceNotFoundException("Goal", "id", goalId);
                });
        validateGoalNotCompleted(persistanceGoal);

        updateBasicProperties(persistanceGoal, updateGoalDto);
        updateRelations(persistanceGoal, updateGoalDto);

        validateMaxActiveGoalsLimit(persistanceGoal);
        persistanceGoal = goalRepository.save(persistanceGoal);
        log.info("Successfully updated goal with id: {}", persistanceGoal.getId());
        return goalMapper.toResponseDto(persistanceGoal);
    }

    private void updateBasicProperties(Goal goal, UpdateGoalDto dto) {
        log.debug("Updating basic properties for goal with id: {}", goal.getId());
        goal.setStatus(GoalStatus.valueOf(dto.status().name()));
        goal.setTitle(dto.title());
        goal.setDescription(dto.description());
        goal.setDeadline(dto.deadline());
    }

    private void updateRelations(Goal goal, UpdateGoalDto dto) {
        log.debug("Updating relations for goal with id: {}", goal.getId());
        setParentGoalIfProvided(goal, dto.parentId());
        setMentorIfProvided(goal, dto.mentorId());
        updateUsersInGoal(goal, dto);
        updateSkillsToAchieveInGoal(goal, dto);
    }

    private void updateSkillsToAchieveInGoal(Goal persistanceGoal, UpdateGoalDto updateGoalDto) {
        log.debug("Updating skills for goal with id: {}", persistanceGoal.getId());
        List<Skill> updatedSkills = skillService.getAllSkillsByIds(updateGoalDto.skillsToAchieveIds());
        if (updatedSkills.isEmpty()) {
            log.warn(NO_SKILLS_FOUND, updateGoalDto.skillsToAchieveIds());
            throw new ResourceNotFoundException("Skill", "id", updateGoalDto.skillsToAchieveIds());
        }
        List<Skill> oldSkills = persistanceGoal.getSkillsToAchieve();
        List<Skill> removedSkills = CollectionUtils.findMissingElements(oldSkills, updatedSkills);
        List<Skill> newSkills = CollectionUtils.findMissingElements(updatedSkills, oldSkills);

        removedSkills.forEach(skill -> skill.removeGoal(persistanceGoal));
        newSkills.forEach(skill -> skill.addGoal(persistanceGoal));
        persistanceGoal.updateSkills(updatedSkills);
    }

    private void updateUsersInGoal(Goal persistanceGoal, UpdateGoalDto updateGoalDto) {
        log.debug("Updating users for goal with id: {}", persistanceGoal.getId());
        List<User> updatedUsers = userService.getAllUsersByIds(updateGoalDto.userIds());
        if (updatedUsers.isEmpty()) {
            log.warn("No users found for provided IDs: {}", updateGoalDto.userIds());
            throw new ResourceNotFoundException("User", "id", updateGoalDto.userIds());
        }
        List<User> oldUsers = persistanceGoal.getUsers();
        List<User> removedUsers = CollectionUtils.findMissingElements(oldUsers, updatedUsers);
        List<User> newUsers = CollectionUtils.findMissingElements(updatedUsers, oldUsers);

        removedUsers.forEach(user -> user.removeGoal(persistanceGoal));
        newUsers.forEach(user -> user.addGoal(persistanceGoal));
        persistanceGoal.updateUsers(updatedUsers);
    }

    private void setParentGoalIfProvided(Goal goal, Long parentId) {
        if (parentId != null) {
            log.debug("Setting parent goal for goal with id: {}", goal.getId());
            Goal parentGoal = goalRepository.findById(parentId)
                    .orElseThrow(() -> {
                        log.warn("Parent goal not found with id: {}", parentId);
                        return new ResourceNotFoundException("Goal", "id", parentId);
                    });
            goal.setParent(parentGoal);
        }
    }

    private void setMentorIfProvided(Goal goal, Long mentorId) {
        if (mentorId != null) {
            log.debug("Setting mentor for goal with id: {}", goal.getId());
            User user = userService.getUserById(mentorId);
            goal.setMentor(user);
        }
    }

    public Page<GoalResponseDto> findSubtasksByGoalId(Long goalId, Pageable pageable) {
        Page<Goal> goals = goalRepository.findAllByParentId(goalId, pageable);
        return goals.map(goalMapper::toResponseDto);
    }

    public Page<GoalResponseDto> findGoalsByFilters(GoalFilterDto filters, Pageable pageable) {
        Page<Goal> goal = goalRepository.findAll(GoalSpecification.build(filters), pageable);
        return goal.map(goalMapper::toResponseDto);
    }

    public Goal findGoalById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id: %s not found".formatted(id)));
    }

    private void validateMaxActiveGoalsLimit(Goal goal) {
        boolean isAnyUserHasMaxNumOfGoals = goal.getUsers().stream()
                .anyMatch(user -> user.hasMaxNumOfGoals(MAX_NUM_ACTIVE_GOALS));
        if (isAnyUserHasMaxNumOfGoals) {
            log.warn(NUMBER_OF_ACTIVE_GOALS_REACHED_FOR_A_USER_IN_GOAL_WITH_ID, goal.getId());
            throw new DataValidationException(MAXIMUM_NUMBER_OF_GOALS_ERROR);
        }
    }

    public Stream<Goal> findGoalsByUserId(long userId) {
        return goalRepository.findGoalsByUserId(userId);
    }

    private void validateGoalNotCompleted(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            log.warn(GOAL_IS_ALREADY_COMPLETED_FOR_GOAL_WITH_ID, goal.getId());
            throw new DataValidationException(GOAL_COMPLETED_ERROR);
        }
    }

    public int countActiveGoalsPerUser(long userId) {
        return goalRepository.countActiveGoalsPerUser(userId);
    }
}
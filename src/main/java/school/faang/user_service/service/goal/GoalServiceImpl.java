package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapperDecorator;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goalvalidator.GoalValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {
    private static final long ACTIVE_GOALS_COUNT = 3;
    private final GoalValidator goalValidator;
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final GoalMapperDecorator goalMapper;
    private final List<GoalFilter> goalFilters;

    @Override
    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goal) {
        validateId(userId);
        goalValidator.validateTitle(goal);
        goalValidator.validateSkills(goal);
        if (goalRepository.countActiveGoalsPerUser(userId) == ACTIVE_GOALS_COUNT) {
            log.error("User can has only {} active goals.", ACTIVE_GOALS_COUNT);
            throw new GoalDataException("The number of allowed active goals has been exceeded. " +
                    "User can has only " + ACTIVE_GOALS_COUNT + " active goals.");
        }
        checkSkillsExistByUserId(userId, goal);
        Long parentId = null;
        if (!Objects.isNull(goal.getParentId())) {
            parentId = goal.getParentId();
        }
        List<Skill> newSkills = getNewSkills(goal);
        saveSkills(newSkills);
        saveUserWithNewGoalAndSkills(userId, goal, newSkills);
        Goal savedGoal = goalRepository.create(goal.getTitle(), goal.getDescription(), parentId);
        return goalMapper.toDto(savedGoal);
    }

    @Override
    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goal) {
        validateId(goalId);
        if (goal.getTitle().isBlank()) {
            log.error("Title can not be null or empty.");
            throw new GoalDataException("Title can not be null or empty.");
        }
        Goal goalById = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Can not update goal. Goal with id {} not found.", goalId);
                    return new GoalDataException("Can not update goal. Goal with id " + goalId + " not found.");
                });
        if (goalById.getStatus().equals(GoalStatus.COMPLETED)) {
            log.error("The goal status should not be completed during the update.");
            throw new GoalDataException("The goal status should not be completed during the update.");
        }
        Goal goalEntity = goalMapper.toEntity(goal);
        goalValidator.validateSkills(goal);
        checkSkillsExistByGoalId(goalId, goalEntity);
        checkStatusSetCompleted(goalId, goal);
        updateSkills(goalById, goal);
        Goal updatedGoal = goalMapper.update(goalById, goal);
        goalRepository.save(updatedGoal);
        return goalMapper.toDto(updatedGoal);
    }

    @Override
    public void deleteGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            log.error("Can not delete goal. Goal with id {} not found.", goalId);
            throw new GoalDataException("Can not delete goal. Goal with " + goalId + " not found.");
        }
        goalRepository.deleteById(goalId);
    }

    @Override
    public List<GoalDto> findSubtasksByGoalId(long goalId, SearchGoalDto searchSubtasksByGoalId) {
        Stream<Goal> subTasks = goalRepository.findByParent(goalId);
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(searchSubtasksByGoalId)) {
                subTasks = goalFilter.apply(subTasks, searchSubtasksByGoalId);
            }
        }
        return subTasks.map(goalMapper::toDto).toList();
    }

    @Override
    public List<GoalDto> getGoalsByUser(long userId, SearchGoalDto searchGoalDto) {
        Stream<Goal> goalsByUserId = Optional.ofNullable(goalRepository.findGoalsByUserId(userId))
                .orElseThrow(() -> {
                    log.error("Goals by user with id {} not found.", userId);
                    return new GoalDataException("Goals by user with id " + userId + " not found.");
                });
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(searchGoalDto)) {
                goalsByUserId = goalFilter.apply(goalsByUserId, searchGoalDto);
            }
        }
        return goalsByUserId.map(goalMapper::toDto).toList();
    }

    private void validateId(Long id) {
        if (Objects.isNull(id)) {
            log.error("Id can not be null.");
            throw new GoalDataException("Id can not be null.");
        }
    }

    private void checkSkillsExistByUserId(Long userId, GoalDto goal) {
        List<Long> goalSkillIds = goal.getSkillIds();
        List<Skill> goalSkills = skillService.findAllSkillsById(goalSkillIds);
        List<Skill> existingSkills = Optional.ofNullable(skillService.findSkillsByUserId(userId))
                .orElseThrow(() -> {
                    log.error("Skills not found for this {} user id.", userId);
                    return new GoalDataException("Skills not found for this " + userId + " user id.");
                });
        goalValidator.validateSkillsExistInBase(goalSkills, existingSkills);
    }

    private List<Skill> getNewSkills(GoalDto goal) {
        List<Long> skillIds = goal.getSkillIds();
        return skillService.findAllSkillsById(skillIds);
    }

    private void saveSkills(List<Skill> newSkills) {
        skillService.saveAllSkills(newSkills);
    }

    private void saveUserWithNewGoalAndSkills(Long userId, GoalDto goal, List<Skill> newSkills) {
        User userFromBase = userService.findUserById(userId);
        if (userFromBase.getGoals() == null) {
            userFromBase.setGoals(new ArrayList<>());
        }

        if (userFromBase.getSkills() == null) {
            userFromBase.setSkills(new ArrayList<>());
        }
        Goal newGoal = goalMapper.toEntity(goal);
        userFromBase.getGoals().add(newGoal);
        userFromBase.getSkills().addAll(new HashSet<>(newSkills));
        userService.saveUser(userFromBase);
    }

    private void checkSkillsExistByGoalId(Long goalId, Goal goal) {
        List<Skill> goalSkills = goal.getSkillsToAchieve();
        List<Skill> existingSkills = Optional.ofNullable(skillService.findSkillsByGoalId(goalId))
                .orElseThrow(() -> {
                    log.error("Skills not found for this {} goal id.", goalId);
                    return new GoalDataException("Skills not found for this " + goalId + " goal id.");
                });
        goalValidator.validateSkillsExistInBase(goalSkills, existingSkills);
    }

    private void updateSkills(Goal goalFromBase, GoalDto goal) {
        goalFromBase.getSkillsToAchieve().clear();
        Goal goalEntity = goalMapper.toEntity(goal);
        List<Skill> newSkills = goalEntity.getSkillsToAchieve();
        goalFromBase.getSkillsToAchieve().addAll(newSkills);
    }

    private void checkStatusSetCompleted(Long goalId, GoalDto goal) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> skillsToAchieveIds = goal.getSkillIds();
            List<Skill> skillsToAchieve = skillService.findAllSkillsById(skillsToAchieveIds);
            List<User> usersByGoalId = goalRepository.findUsersByGoalId(goalId);
            usersByGoalId.forEach(user -> user.getSkills().addAll(new HashSet<>(skillsToAchieve)));
        }
    }
}

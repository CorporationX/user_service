package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.model.goal.GoalFilter;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.aspect.score.TrackActionScore;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final UserContext userContext;
    private final GoalValidator goalValidator;


    @Transactional(readOnly = true)
    public Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Goal with id {} not found", goalId);
                    return new GoalNotFoundException(goalId);
                });
    }

    @Transactional
    public Goal createGoal(final Goal goal, final Long parentId, final List<Long> skillIds) {
        long userId = userContext.getUserId();

        int countActiveGoalForUser = goalRepository.countActiveGoalsPerUser(userId);
        goalValidator.checkCountGoalForUser(userId, countActiveGoalForUser);

        User owner = userService.getUserByIdOrThrow(userId);
        List<User> users = new ArrayList<>();
        users.add(owner);
        goal.setUsers(users);

        if (parentId != null) {
            Goal parentGoal = getGoalById(parentId);
            goal.setParent(parentGoal);
        }

        setSkills(goal, skillIds);

        goal.setStatus(GoalStatus.ACTIVE);

        Goal savedGoal = goalRepository.save(goal);
        log.info("Goal {} has been saved", savedGoal);

        return savedGoal;
    }

    @Transactional
    public Goal updateGoal(final Goal goal, final List<Long> skillIds) {
        setSkills(goal, skillIds);

        Goal saveGoal = goalRepository.save(goal);
        log.info("Goal {} has been update", saveGoal);

        if (Objects.equals(goal.getStatus(), GoalStatus.COMPLETED)) {
            completeGoal(saveGoal);
        }

        return saveGoal;
    }

    @Transactional
    public void deleteGoalById(long goalId) {
        getGoalById(goalId);

        goalRepository.deleteById(goalId);
        log.info("Goal with id {} has been deleted", goalId);
    }

    @Transactional(readOnly = true)
    public List<Goal> getSubtasksByParentGoalId(long goalParentId) {
        try (Stream<Goal> goalsStream = goalRepository.findByParent(goalParentId)) {
            return goalsStream.toList();
        }
    }


    @Transactional(readOnly = true)
    public List<Goal> getGoalsByUserAndFilter(GoalFilter filter) {
        long userId = userContext.getUserId();

        try (Stream<Goal> goalsStream = goalRepository.findGoalsByUserId(userId)) {
            return goalsStream
                    .filter(goal -> Objects.equals(goal.getTitle(), filter.getTitle()))
                    .filter(goal -> Objects.equals(goal.getStatus(), filter.getStatus()))
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public Goal getGoalByIdIfActiveElseThrow(long goalId) {
        Goal goal = getGoalById(goalId);
        goalValidator.checkGoalIsCompleted(goal);
        return goal;
    }

    @TrackActionScore(ScoreActionType.COMPLETE_GOAL)
    private void completeGoal(Goal goal) {
        List<Long> userIds = goal.getUsers().stream()
                .map(User::getId)
                .toList();
        List<Long> skillIds = goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();
        skillService.assignSkillsToUsers(skillIds, userIds);
    }

    private void setSkills(Goal goal, List<Long> skillIds) {
        List<Skill> skills = skillIds.stream()
                .map(skillService::getSkillByIdOrThrow)
                .collect(Collectors.toList());
        goal.setSkillsToAchieve(skills);
    }
}

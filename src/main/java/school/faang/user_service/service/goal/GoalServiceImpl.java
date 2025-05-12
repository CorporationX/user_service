package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.goal.GoalUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    public static int MAXIMUM_ALLOWED_ACTIVE_GOALS = 3;//todo вынести в конфигурацию компонента
    private final GoalMapper goalMapper;
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final List<GoalFilter> goalFilters;


    @Override
    public GoalDto createGoal(Long userId, Goal goal) {
        long usersActiveGoals = goalRepository.findGoalsByUserId(userId)
                .filter(GoalService::goalIsActive)
                .count();

        if (usersActiveGoals >= MAXIMUM_ALLOWED_ACTIVE_GOALS) {
            throw new IllegalArgumentException("User exceeded maximum allowed number or active goals "
                    + usersActiveGoals);
        }

        List<Skill> skillsOfUser = skillService.findAllByUserId(userId);
        List<Skill> missingSkills = goal.getSkillsToAchieve().stream()
                .filter(skillsOfUser::contains)
                .toList();

        if (!missingSkills.isEmpty()) {
            throw new IllegalArgumentException("User hasn't required skills for the goal: " + missingSkills);
        }
        Goal createdGoal = goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                goal.getParent().getId()
        );

        addGoalToUser(userId, createdGoal);
        addGoalToSkills(createdGoal);

        return goalMapper.goalToGoalDTO(createdGoal);
    }

    @Override
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(NoSuchElementException::new);

        boolean goalWasCompleted = goalToUpdate.getStatus() == GoalStatus.COMPLETED;
        boolean goalSetCompleted = goalDto.getStatus() == GoalStatus.COMPLETED;
        if (goalSetCompleted && goalWasCompleted)
            throw new IllegalStateException("Goal was already completed");

        List<Long> copyOfUpdatesSkillIds = new ArrayList<>(goalDto.getSkillIds());
        copyOfUpdatesSkillIds.removeAll(
                skillService.findAllByIds(copyOfUpdatesSkillIds).stream()
                        .map(Skill::getId)
                        .toList()
        );
        if (!copyOfUpdatesSkillIds.isEmpty())
            throw new IllegalArgumentException("Skill ids not exists: ".formatted(copyOfUpdatesSkillIds.toArray()));

        goalMapper.updateGoalFromDto(goalDto, goalToUpdate);
        GoalUtil.updateTime(goalToUpdate, LocalDateTime.now());
        goalRepository.save(goalToUpdate);

        if (GoalStatus.COMPLETED == goalDto.getStatus()) {
            updateUsersWithSkills(goalToUpdate);
        }

        return goalMapper.goalToGoalDTO(goalToUpdate);
    }

    @Transactional
    void updateUsersWithSkills(Goal completedGoal) {
        List<User> users = completedGoal.getUsers();
        List<Skill> skills = completedGoal.getSkillsToAchieve();
        users.forEach(user -> {
            var merged = new HashSet<>(skills);
            merged.addAll(user.getSkills());
            user.setSkills(new ArrayList<>(merged));
        });
        skills.forEach(skill -> {
            var merged = new HashSet<>(users);
            merged.addAll(skill.getUsers());
            skill.setUsers(new ArrayList<>(merged));
        });
        skillService.updateAll(skills);
        userService.updateAll(users);
    }

    @Override
    public GoalDto deleteGoal(long goalId) {
        Goal goalToDelete = goalRepository.findById(goalId)
                .orElseThrow(NoSuchElementException::new);
        goalRepository.delete(goalToDelete);
        deleteGoalCascade(goalToDelete);

        return goalMapper.goalToGoalDTO(goalToDelete);
    }

    private void deleteGoalCascade(Goal goalToDelete) {
        List<User> users = goalToDelete.getUsers();
        users.forEach(user -> user.getGoals().remove(goalToDelete));
        userService.updateAll(users);

        List<Skill> skills = goalToDelete.getSkillsToAchieve();
        skills.forEach(skill -> skill.getGoals().remove(goalToDelete));
        skillService.updateAll(skills);

        List<GoalInvitation> invitations = goalToDelete.getInvitations();
        //todo on next task with invitations
    }

    @Override
    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        GoalFilterDto blankFilter = new GoalFilterDto();
        return findSubtasksByGoalId(goalId, blankFilter);
    }

    @Override
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        Stream<Goal> goalsByParent = goalRepository.findByParent(goalId);
        List<Goal> goals = filterGoals(goalsByParent, filter);
        return goalMapper.mapGoalsToDTOs(goals);
    }

    @Override
    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter) {
        Stream<Goal> goalsByUserId = goalRepository.findGoalsByUserId(userId);
        List<Goal> goals = filterGoals(goalsByUserId, filter);
        return goalMapper.mapGoalsToDTOs(goals);
    }

    private List<Goal> filterGoals(Stream<Goal> goalStream, GoalFilterDto filterDto) {
        goalFilters.forEach(goalFilter -> goalFilter.setCriteria(filterDto));
        List<GoalFilter> applicableFilters = this.goalFilters.stream()
                .filter(GoalFilter::isApplicable)
                .toList();
        return goalStream
                .filter(goal ->
                        applicableFilters.stream()
                                .allMatch(goalFilter -> goalFilter.doFilter(goal))
                )
                .toList();
    }

    @Override
    public Goal findById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    private User addGoalToUser(Long userId, Goal createdGoal) {
        User userById = userService.findById(userId);
        return addGoalToUser(userById, createdGoal);
    }

    private User addGoalToUser(User user, Goal createdGoal) {
        List<Goal> goals = user.getGoals();
        goals.add(createdGoal);
        user.setGoals(goals);
        return userService.updateUser(user);
    }

    private void addGoalToSkills(Goal createdGoal) {
        List<Skill> skillsToUpdateWithNewGoal = createdGoal.getSkillsToAchieve();
        skillsToUpdateWithNewGoal.forEach(skill -> {
            List<Goal> skillGoals = skill.getGoals();
            skillGoals.add(createdGoal);
            skill.setGoals(skillGoals);
        });
        skillService.updateAll(skillsToUpdateWithNewGoal);
    }

}

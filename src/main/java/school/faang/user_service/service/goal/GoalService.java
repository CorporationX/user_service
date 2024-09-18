package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalServiceValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalServiceValidator goalServiceValidator;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        User user = userService.getUserById(userId);
        validateUserGoalLimit(userId);

        Goal goal = createGoalEntity(goalDto, user);

        if (hasSkills(goalDto)) {
            setGoalSkills(goal, goalDto, userId);
        }

        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal existingGoal = getGoalById(goalId);
        Goal updatedGoal = updateGoalEntity(existingGoal, goalDto);

        return goalMapper.toGoalDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = getGoalById(goalId);

        if (goal != null) {
            goalRepository.deleteById(goalId);
        }
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, String filterTitle, String filterDescription, GoalStatus filterStatus, List<Long> filterSkills) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        GoalFilterDto filterDto = createFilterDto(filterTitle, filterDescription, filterStatus, filterSkills);

        return filterGoals(goals, filterDto);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(long userId, String filterTitle, String filterDescription, GoalStatus filterStatus, List<Long> filterSkills) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        GoalFilterDto filterDto = createFilterDto(filterTitle, filterDescription, filterStatus, filterSkills);

        return filterGoals(goals, filterDto);
    }

    private Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal with this id does not exist in the database"));
    }

    private void validateUserGoalLimit(Long userId) {
        int userGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        goalServiceValidator.validateUserGoalLimit(userGoalsCount);
    }

    private Goal createGoalEntity(GoalDto goalDto, User user) {
        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        goalRepository.addGoalToUserId(goal.getId(), user.getId());
        goal.setUsers(List.of(user));

        return goal;
    }

    private Goal updateGoalEntity(Goal existingGoal, GoalDto goalDto) {
        goalServiceValidator.validateGoalStatusNotCompleted(existingGoal);

        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());

        if (goalDto.getStatus() != null) {
            existingGoal.setStatus(goalDto.getStatus());
        }

        if (goalDto.getParentId() != null) {
            goalRepository.findById(goalDto.getParentId()).ifPresent(existingGoal::setParent);
        }

        goalRepository.save(existingGoal);

        if (hasSkills(goalDto)) {
            updateGoalSkills(existingGoal, goalDto);
        }

        return existingGoal;
    }

    private boolean hasSkills(GoalDto goalDto) {
        return goalDto.getSkillIds() != null && !goalDto.getSkillIds().isEmpty();
    }

    private void setGoalSkills(Goal goal, GoalDto goalDto, Long userId) {
        List<Long> skillIds = goalDto.getSkillIds();

        skillService.addSkillsToUserId(skillIds, userId);
        skillService.addSkillsToGoal(skillIds, goal);
    }

    private void updateGoalSkills(Goal goal, GoalDto goalDto) {
        List<Long> newSkillIds = goalDto.getSkillIds();
        List<Skill> skillsToAchieve = goal.getSkillsToAchieve() ;

        if (newSkillIds != null) {
            List<Long> oldSkillIds = skillsToAchieve != null
                    ? goal.getSkillsToAchieve().stream().map(Skill::getId).toList()
                    : List.of();

            List<Long> removedSkillIds = oldSkillIds.stream()
                    .filter(skillId -> !newSkillIds.contains(skillId))
                    .toList();

            List<Long> addedSkillIds = newSkillIds.stream()
                    .filter(skillId -> !oldSkillIds.contains(skillId))
                    .toList();

            goal.getUsers().forEach(user -> {
                skillService.removeSkillsFromUserId(removedSkillIds, user.getId());
                skillService.addSkillsToUserId(addedSkillIds, user.getId());
            });

            skillService.removeSkillsFromGoal(addedSkillIds, goal);
            skillService.addSkillsToGoal(addedSkillIds, goal);
        }
    }

    private GoalFilterDto createFilterDto(String filterTitle, String filterDescription, GoalStatus filterStatus, List<Long> filterSkills) {
        return new GoalFilterDto(filterTitle, filterDescription, filterStatus, filterSkills);
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goals, (currentGoals, filter) -> filter.apply(currentGoals, filterDto), (s1, s2) -> s1)
                .map(goalMapper::toGoalDto)
                .toList();
    }
}



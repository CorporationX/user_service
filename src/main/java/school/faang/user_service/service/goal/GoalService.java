package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
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
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalServiceValidator goalServiceValidator;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        int userGoalsCount = goalRepository.countActiveGoalsPerUser(userId);

        goalServiceValidator.validateUserGoalLimit(userGoalsCount);

        goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        skillService.create(goalMapper.toGoal(goalDto).getSkillsToAchieve(), userId);
        return goalDto;
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal with this id does not exist in the database"));
        Goal updateGoal = goalMapper.toGoal(goalDto);

        goalServiceValidator.validateGoalStatusNotCompleted(goal);
        goalServiceValidator.validateSkillsExistByTitle(updateGoal.getSkillsToAchieve());

        goal.setStatus(updateGoal.getStatus());
        goal.setTitle(updateGoal.getTitle());
        goal.setSkillsToAchieve(updateGoal.getSkillsToAchieve());
        goal.setDescription(updateGoal.getDescription());

        goalRepository.save(goal);
        skillService.addSkillToUsers(goalRepository.findUsersByGoalId(goalId), goalId);
        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        goalServiceValidator.validateGoalsExist(goal);

        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        return filterGoals(goals, filterDto);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        return filterGoals(goals, filterDto);
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goals, (currentGoals, filter) -> filter.apply(currentGoals, filterDto), (s1, s2) -> s1)
                .map(goalMapper::toGoalDto)
                .toList();
    }
}


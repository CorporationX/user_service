package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.filter.GoalFilters;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalServiceValidate;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final List<GoalFilters> goalFilters;
    private final GoalServiceValidate validator;

    public void createGoal(Long userId, Goal goal) {
        List<String> allGoalTitles = findAllGoalTitles();
        int countActiveUser = goalRepository.countActiveGoalsPerUser(userId);
        validator.validateCreateGoal(userId, goal, countActiveUser, allGoalTitles);

        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        skillService.create(goal.getSkillsToAchieve(), userId);
    }

    public void updateGoal(long goalId, GoalDto goalDto) {
        String status = goalRepository.findByParent(goalId)
                .map(Goal::getStatus)
                .toString();
        Goal goal = goalMapper.toGoal(goalDto);
        validator.validateUpdateGoal(goal, status);

        skillService.addSkillToUsers(goalRepository.findUsersByGoalId(goalId), goalId);
    }

    public void deleteGoal(long goalId) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        validator.validateDeleteGoal(goal);

        goalRepository.deleteByGoalId(goalId);
    }

    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        return filters(goal, filters);
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filters) {
        Stream<Goal> goal = findGoalsByUserId(userId);
        return filters(goal, filters);
    }

    private Stream<Goal> findGoalsByUserId(long userId) {
        return goalRepository.findGoalsByUserId(userId);
    }

    private List<GoalDto> filters(Stream<Goal> goal, GoalFilterDto filters) {
        goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(goal, filters));

        return goal
                .map(goalMapper::toGoalDto)
                .toList();
    }

    private List<String> findAllGoalTitles() {
        return goalRepository.findAllGoalTitles();
    }
}
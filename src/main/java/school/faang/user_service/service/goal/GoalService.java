package school.faang.user_service.service.goal;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.filter.GoalFilter;
import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@Getter
@AllArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final static int MAX_AMOUNT_OF_ACTIVE_GOALS = 3;


    public void createGoal(Long userId, Goal goal) {
        if (goal.getTitle() == null) {
            throw new IllegalArgumentException("Goal doesn't have a title!");
        }
        long amountOfActiveGoals = goalRepository
                .findGoalsByUserId(userId)
                .filter(
                        goal1 ->
                                goal1.getStatus() == GoalStatus.ACTIVE)
                .count();
        if (amountOfActiveGoals > MAX_AMOUNT_OF_ACTIVE_GOALS) {
            throw new IllegalArgumentException("More than 3 active goals!");
        }
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        Optional<Goal> currentGoal = goalRepository.findById(goal.getId());
        currentGoal.ifPresent(goal1 -> goal1.setSkillsToAchieve(goal.getSkillsToAchieve()));
    }

    public Stream<Goal> findGoalsByUserId(Long userId) {
        //TODO: Skill check and tests
        return goalRepository.findGoalsByUserId(userId);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        List<GoalFilter> list = goalFilters.stream().filter(filter -> filter.isApplicable(filters)).toList();
        Stream<Goal> goals = this.findGoalsByUserId(userId);
        for (GoalFilter filter : list) {
            goals = filter.apply(goals, filters);
        }
        return goals.map(goalMapper::goalToGoalDto).toList();

    }

    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        List<GoalFilter> list = goalFilters.stream().filter(filter -> filter.isApplicable(filters)).toList();
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        for (GoalFilter filter : list) {
            goals = filter.apply(goals, filters);
        }
        return goals.map(goalMapper::goalToGoalDto).toList();
    }

    public void deleteGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new IllegalArgumentException("Goal already delete!");
        }
        goalRepository.deleteById(goalId);
    }
}
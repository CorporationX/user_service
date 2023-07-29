package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public List<GoalDto> getGoalsByUser(@NotNull Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findAll().stream();

        return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .flatMap(goalFilter -> goalFilter.applyFilter(goals,filters))
                .map(goalMapper::toDto)
                .toList();
    }

    public List<GoalDto> getGoalsByUser(@NotNull Long userId) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        return goals.map(goalMapper::toDto).toList();
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    public void deleteAllByIds(List<Long> ids) {
        goalRepository.deleteAllById(ids);
    }

    public GoalDto get(Long id)  {
        Goal goal = goalRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Couldn't find a goal with id: " + id));

        return goalMapper.toDto(goal);
    }

    public GoalDto update(GoalDto goal) {
        GoalDto existingGoal = get(goal.getId());

        goalMapper.update(existingGoal, goal);
        goalRepository.save(goalMapper.toEntity(existingGoal));

        return existingGoal;
    }

    public int removeUserFromGoals(List<Long> goalIds, Long userId) {
        List<Goal> goals = goalRepository.findAllById(goalIds);

        goals.forEach(goal -> {
            List<User> currentUsers = goal.getUsers();
            goal.setUsers(currentUsers.stream().filter(user -> user.getId() != userId).toList());
        });

        return goals.size();
    }
}

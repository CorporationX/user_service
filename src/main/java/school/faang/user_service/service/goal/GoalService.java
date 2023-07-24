package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public void deleteGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new IllegalArgumentException("Goal is not found");
        }

        goalRepository.deleteById(goalId);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())));

        if (filterDto != null) {
            return filterGoals(goals, filterDto);
        }

        return goalMapper.toDtoList(goals.toList());
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getSubGoalsByFilter(Long parentId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(parentId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())));

        if (filterDto != null) {
            return filterGoals(goals, filterDto);
        }

        return goalMapper.toDtoList(goals.toList());
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filter) {
        Stream<Goal> filteredGoals = goals;

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                filteredGoals = goalFilter.apply(filteredGoals, filter);
            }
        }

        return goalMapper.toDtoList(filteredGoals.toList());
    }
}

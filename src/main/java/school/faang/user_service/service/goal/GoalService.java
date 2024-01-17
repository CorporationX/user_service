package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final SkillService skillService;

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        Stream<Goal> foundGoals = goalRepository.findByParent(goalId);
        List<Goal> filteredGoals = filterGoals(foundGoals, filter).toList();

        for (Goal goal : filteredGoals) {
            goal.setSkillsToAchieve(skillService.findSkillsByGoalId(goal.getId()));
        }

        return filteredGoals.stream().map(goalMapper::toDto).toList();
    }

    public Stream<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filter) {
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                goals = goalFilter.filter(goals, filter);
            }
        }
        return goals;
    }
}

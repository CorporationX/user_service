package school.faang.user_service.filter;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GoalFilter {
    private final GoalFilterDto goalFilterDto;
    private final GoalMapper goalMapper;

    public GoalFilter(GoalFilterDto filterDto, GoalMapper goalMapper) {
        this.goalFilterDto = filterDto;
        this.goalMapper = goalMapper;
    }

    private boolean isApplicable(Goal goal) {
        if (goalFilterDto.getGoalStatus() != null && !goal.getStatus().equals(goalFilterDto.getGoalStatus())) {
            return false;
        }

        if (goalFilterDto.getSkillId() != null && !goalContainSkillId(goal)) {
            return false;
        }

        if (goalFilterDto.getTitle() != null && !goal.getTitle().equals(goalFilterDto.getTitle())) {
            return false;
        }

        return true;
    }

    private boolean goalContainSkillId(Goal goal) {
        return goal.getSkillsToAchieve()
                .stream()
                .map(Skill::getId)
                .toList()
                .contains(goalFilterDto.getSkillId());
    }


    private Stream<Goal> applyFilter(Stream<Goal> goals) {
        return goals.filter(this::isApplicable);
    }

    public List<GoalDto> filterGoals(Stream<Goal> goals) {
        return applyFilter(goals)
                .map(goalMapper::toGoalDto)
                .collect(Collectors.toList());
    }
}

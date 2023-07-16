package school.faang.user_service.service.goal.filters;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalFilter;

import java.util.List;

public class TitleGoalFilter implements GoalFilter {
    private final String title;

    public TitleGoalFilter(String title) {
        this.title = title;
    }

    @Override
    public List<GoalDto> applyFilter(List<GoalDto> goals) {
        return goals.stream()
                .filter(goal -> title.equals(goal.getTitle())&& isApplicable(goal))
                .toList();
    }

    @Override
    public boolean isApplicable(GoalDto goalDto) {
        return goalDto.getTitle() != null;
    }
}
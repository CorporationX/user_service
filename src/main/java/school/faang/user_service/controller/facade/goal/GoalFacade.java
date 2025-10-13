package school.faang.user_service.controller.facade.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalFacade {

    private final GoalService goalService;
    private final GoalMapper goalMapper;

    public GoalDto create(CreateGoalDto createGoalDto) {
        Goal goal = goalMapper.toGoal(createGoalDto);
        Goal result = goalService.create(goal, createGoalDto.userIds(),
                createGoalDto.skillIds(), createGoalDto.mentorId());
        return goalMapper.toGoalDto(result);
    }

    public GoalDto update(Long goalId, GoalUpdateDto updateGoalDto) {
        Goal result = goalService.update(goalId, updateGoalDto);
        return goalMapper.toGoalDto(result);
    }


    public List<GoalDto> filters(GoalFilterDto filters) {
        List<Goal> goals = goalService.getByFilters(filters);

        return goals.stream()
                .map(goalMapper::toGoalDto)
                .toList();
    }

    public void delete(Long goalId) {
        goalService.delete(goalId);
    }
}

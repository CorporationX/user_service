package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;
    private final GoalMapper goalMapper;


    public List<Goal> getGoalsByUser(Long userId, GoalFilterDto filter) {
        if (userId < 1) throw new IllegalArgumentException("userId can not be less than 1");
        if (filter == null) return service.findGoalsByUserId(userId);
        return service.findGoalsByUserId(userId).stream()
                .map(goalMapper::toDto)
                .filter(goalDto -> {
                    boolean res = true;
                    if(filter.getDescription() != null && !filter.getDescription().isBlank()) res = res && filter.getDescription().equals(goalDto.getDescription());
                    if(filter.getParentId() != null) res = res && filter.getParentId().equals(goalDto.getParentId());
                    if(filter.getTitle() != null && !filter.getTitle().isBlank()) res = res && filter.getTitle().equals(goalDto.getTitle());
                    if(filter.getStatus() != null) res = res && filter.getStatus().equals(goalDto.getStatus());
                    if(filter.getSkillIds() != null && !filter.getSkillIds().isEmpty()) res = res && filter.getSkillIds().equals(goalDto.getSkillIds());
                    return res;})
                .map(goalMapper::toEntity)
                .toList();
    }
}

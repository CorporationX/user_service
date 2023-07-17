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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    @Transactional(readOnly = true)
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filterDto) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())))
                .collect(Collectors.toList());
        if (goalFilters != null){
            goalFilters.stream()
                    .filter(filter -> filter.isApplicable(filterDto))
                    .forEach(filter -> filter.apply(goals, filterDto));
        }

        return goalMapper.toDtoList(goals);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getSubGoalsByFilter(Long parentId, GoalFilterDto filterDto) {
        List<Goal> goals = goalRepository.findByParent(parentId)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId())))
                .collect(Collectors.toList());
        if (goalFilters != null){
            goalFilters.stream()
                    .filter(filter -> filter.isApplicable(filterDto))
                    .forEach(filter -> filter.apply(goals, filterDto));
        }

        return goalMapper.toDtoList(goals);
    }
}

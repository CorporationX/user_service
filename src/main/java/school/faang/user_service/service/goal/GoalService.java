package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filterList;
    private final int MAX_ACTIVE_GOALS = 3;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        List<GoalDto> dtoList = goalMapper.goalsToDtos(findGoalsByUserId(userId));
        if (filter != null) {
            filterList.stream()
                    .filter((fil) -> fil.isApplicable(filter))
                    .forEach((fil) -> fil.apply(dtoList, filter));
        }
        return dtoList;
    }

    public List<GoalDto> getSubGoalsByUser(Long parentId, GoalFilterDto filter) {
        List<GoalDto> dtoList = goalMapper.goalsToDtos(findSubGoalsByParentId(parentId));
        if (filter != null) {
            filterList.stream()
                    .filter((fil) -> fil.isApplicable(filter))
                    .forEach((fil) -> fil.apply(dtoList, filter));
        }
        return dtoList;
    }

    public List<Goal> findGoalsByUserId(long id) {
        //if (id == null) throw new DataValidationException("userId can not be Null");
        if (id < 1) throw new DataValidationException("userId can not be less than 1");
        return goalRepository.findGoalsByUserId(id)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId()))).toList();
    }

    public List<Goal> findSubGoalsByParentId(long id) {
        //if (id == null) throw new DataValidationException("parentId can not be Null");
        if (id < 1) {
            throw new DataValidationException("parentId can not be less than 1");
        }
        return goalRepository.findByParent(id)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId()))).toList();
    }
}

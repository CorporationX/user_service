package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalValidator validator;
    private final UserContext userContext;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filterList;

    public GoalDto createGoal(GoalDto goalDto) {
        long userId = userContext.getUserId();
        validator.creatingGoalServiceValidation(userId, goalDto);
        goalDto.setUserIds(List.of(userId));
        goalRepository.save(goalMapper.toEntity(goalDto));
        return goalDto;
    }

    public GoalDto updateGoal(long id, GoalDto goalDto) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Goal not found"));
        validator.updateGoalServiceValidation(goal, goalDto);

        goal = goalMapper.toEntity(goalDto);

        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> skillIds = goalDto.getSkillIds();
            goalRepository.findUsersByGoalId(id).forEach(user -> {
                skillIds.forEach(skillId -> skillRepository.assignSkillToUser(skillId, user.getId()));
            });
        } else {
            return goalMapper.toDto(goalRepository.save(goal));
        }

        return null;
    }

    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new EntityNotFoundException("Goal does not exist");
        }
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        List<GoalDto> dtoList = goalMapper.goalsToDtos(findGoalsByUserId(userId));
        filter(filter, dtoList);
        return dtoList;
    }

    public List<GoalDto> getSubGoalsByUser(long parentId, GoalFilterDto filter) {
        List<GoalDto> dtoList = goalMapper.goalsToDtos(findSubGoalsByParentId(parentId));
        filter(filter, dtoList);
        return dtoList;
    }

    public List<Goal> findGoalsByUserId(long id) {
        if (id < 1) {
            throw new DataValidationException("userId can not be less than 1");
        }
        return goalRepository.findGoalsByUserId(id)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId()))).toList();
    }

    public List<Goal> findSubGoalsByParentId(long id) {
        if (id < 1) {
            throw new DataValidationException("parentId can not be less than 1");
        }
        return goalRepository.findByParent(id)
                .peek(goal -> goal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(goal.getId()))).toList();
    }

    public void filter(GoalFilterDto filter, List<GoalDto> dtoList) {
        if (filter != null) {
            filterList.stream()
                    .filter((fil) -> fil.isApplicable(filter))
                    .forEach((fil) -> fil.apply(dtoList, filter));
        }
    }
}

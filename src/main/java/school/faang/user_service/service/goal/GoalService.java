package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.GoalFilter;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ilia Chuvatkin
 */

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalValidator goalValidator;
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public void createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateForSkillsAndActiveGoals(userId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setUsers(List.of(userService.getUserById(userId)));
        setSetSkillsToAchieve(goalDto, goal);
        goalRepository.save(goal);
    }

    public void setSetSkillsToAchieve(GoalDto goalDto, Goal goal) {
        goal.setSkillsToAchieve(goalDto.getSkillIds().stream().map(skillService::getSkillIfExists).toList());
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalNew = goalMapper.toEntity(goalDto);
        setSetSkillsToAchieve(goalDto, goalNew);
        Goal goalOld = getGoalById(goalId);
        goalValidator.validateByCompleted(goalOld);
        goalValidator.validateByExistingSkills(goalNew);

        if (goalNew.getStatus() == GoalStatus.COMPLETED) {
            goalRepository.findUsersByGoalId(goalNew.getId())
                    .forEach(user -> goalNew.getSkillsToAchieve().stream()
                            .filter(skill -> !user.getSkills().contains(skill))
                            .forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId())));
        }
        Goal goalResult = goalRepository.save(goalNew);
        return goalMapper.toDto(goalResult);
    }

    public Goal getGoalById(long id) {
        return goalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Goal with id " + id + " is not exists"));
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findGoalsByUser(Long userId, GoalFilterDto filter) {
        List<Goal> userGoals = getGoalsByUserId(userId);
        return getGoalsByFilter(userGoals, filter);
    }

    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findGoalsByUserId(userId).collect(Collectors.toList());
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        List<Goal> goals = goalRepository.findByParent(goalId).collect(Collectors.toList());
        return getGoalsByFilter(goals, filter);
    }

    private List<GoalDto> getGoalsByFilter(List<Goal> goals, GoalFilterDto filter) {
        goalFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(goals, filter));
        return goals.stream().map(goalMapper::toDto).toList();
    }

    public void existsGoalById(long id) {
        if (!goalRepository.existsById(id)) {
            throw new EntityNotFoundException("Goal not found");
        }
    }
}

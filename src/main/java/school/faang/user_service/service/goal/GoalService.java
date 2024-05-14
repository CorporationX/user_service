package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;

import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.EntityNotFoundException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidation;



import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class GoalService {
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;
    private final GoalValidation goalValidation;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final SkillService skillService;
    private final UserService userService;

    public void createGoal(Long userId, GoalDto goalDto) {
        goalValidation.validateForSkillsAndActiveGoals(userId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setUsers(List.of(userService.findById(userId)));
        setSetSkillsToAchieve(goalDto, goal);
        goalRepository.save(goal);
    }

    public void setSetSkillsToAchieve(GoalDto goalDto, Goal goal) {
        goal.setSkillsToAchieve(goalDto.getSkillId().stream().map(skillService::findById).toList());
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalNew = goalMapper.toEntity(goalDto);
        setSetSkillsToAchieve(goalDto, goalNew);
        Goal goalOld = getGoalById(goalId);
        goalValidation.validateByCompleted(goalOld);
        goalValidation.validateByExistingSkills(goalNew);

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

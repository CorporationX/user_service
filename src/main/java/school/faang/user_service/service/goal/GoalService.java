package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalDtoMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.validation.GoalServiceValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> filters;
    private final GoalDtoMapper goalDtoMapper;
    private final GoalServiceValidator goalServiceValidator;

    @Transactional
    public GoalDto createGoal(long userId, GoalDto goalDto) {
        goalServiceValidator.validateCreation(userId, goalDto);
        Goal goal = goalDtoMapper.toEntity(goalDto);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUsers(userRepository.findAllById(goalDto.getUsersId()));
        goal.setSkillsToAchieve(skillRepository.findAllById(goalDto.getSkillsToAchieveId()));
        return goalDtoMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        goalServiceValidator.validateUpdating(goalId, goalDto);
        Goal goal = goalDtoMapper.toEntity(goalDto);
        goal.setId(goalId);
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            updateUsersAndSkillsWhenGoalCompleted(goal);
        }
        goal.setUsers(userRepository.findAllById(goalDto.getUsersId()));
        goal.setSkillsToAchieve(skillRepository.findAllById(goalDto.getSkillsToAchieveId()));
        return goalDtoMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalServiceValidator.validateDeleting(goalId);
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        goalServiceValidator.validateFindingSubtasksByGoalId(goalId);
        return getFilteredGoals(filter);
    }

    @Transactional
    public List<GoalDto> findGoalsByUserId(long userId, GoalFilterDto filter) {
        goalServiceValidator.validateFindingGoalsByUserId(userId);
        return getFilteredGoals(filter);
    }

    private List<GoalDto> getFilteredGoals(GoalFilterDto filter) {
        List<GoalFilter> applicableFilters = getApplicableGoalFilters(filter);
        List<Goal> filteredGoals = goalRepository.findAll();
        for (GoalFilter applicableFilter : applicableFilters) {
            filteredGoals = applicableFilter.apply(filteredGoals.stream(), filter).toList();
        }
        return goalDtoMapper.toDtos(filteredGoals);
    }

    private void updateUsersAndSkillsWhenGoalCompleted(Goal goal) {
        Optional<Goal> entityOpt = goalRepository.findById(goal.getId());
        if (entityOpt.isPresent()) {
            Goal goalEntity = entityOpt.get();
            goalEntity.getUsers().forEach(user -> {
                List<Skill> currentUserSkills = user.getSkills();
                currentUserSkills.addAll(skillRepository.findSkillsByGoalId(goal.getId()));
                user.setSkills(currentUserSkills);
                userRepository.save(user);
                currentUserSkills.forEach(skill -> skillRepository.assignSkillToUser(user.getId(), skill.getId()));
            });
        }
    }

    private List<GoalFilter> getApplicableGoalFilters(GoalFilterDto filter) {
        return filters.stream()
                .filter(f -> f.isApplicable(filter))
                .toList();
    }
}

package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.GoalMessagePublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.GoalValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final List<GoalFilter> filters;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillService skillService;
    private final UserService userService;
    private final GoalMessagePublisher goalMessagePublisher;

    @Transactional
    public GoalDto createGoal(long userId, GoalDto goalDto) {
        goalValidator.validateCreation(userId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setStatus(GoalStatus.ACTIVE);
        setUsersAndSkills(goalDto, goal);
        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        goalValidator.validateUpdating(goalId, goalDto);
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setId(goalId);
        if (goal.getStatus() != null && goal.getStatus().equals(GoalStatus.COMPLETED)) {
            updateUsersAndSkillsWhenGoalCompleted(goal);
        }
        setUsersAndSkills(goalDto, goal);
        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        goalValidator.validateGoalExistence(goalId);
        return getFilteredGoals(filter);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> findGoalsByUserId(long userId, GoalFilterDto filter) {
        goalValidator.validateUserExistence(userId);
        return getFilteredGoals(filter);
    }

    private void setUsersAndSkills(GoalDto goalDto, Goal goal) {
        if (goalDto.getUserIds() != null && !goalDto.getUserIds().isEmpty()) {
            goal.setUsers(userService.findAllById(goalDto.getUserIds()));
        }
        goal.setSkillsToAchieve(skillService.findAllById(goalDto.getSkillsToAchieveIds()));
    }

    private void updateUsersAndSkillsWhenGoalCompleted(Goal goal) {
        Optional<Goal> entityOpt = goalRepository.findById(goal.getId());
        if (entityOpt.isPresent()) {
            Goal goalEntity = entityOpt.get();
            sentGoalAnalytic(goalEntity);
            goalEntity.getUsers().forEach(user -> {
                List<Skill> currentUserSkills;
                if (user.getSkills() != null) {
                    currentUserSkills = user.getSkills();
                } else {
                    currentUserSkills = new ArrayList<>();
                }
                    currentUserSkills.addAll(skillService.findSkillsByGoalId(goal.getId()));
                    user.setSkills(currentUserSkills);
                    userService.save(user);
                    currentUserSkills.forEach(skill -> skillService.assignSkillToUser(user.getId(), skill.getId()));
            });
        }
    }

    private List<GoalDto> getFilteredGoals(GoalFilterDto filter) {
        List<GoalFilter> applicableFilters = getApplicableGoalFilters(filter);
        List<Goal> filteredGoals = goalRepository.findAll();
        for (GoalFilter applicableFilter : applicableFilters) {
            filteredGoals = applicableFilter.apply(filteredGoals.stream(), filter).toList();
        }
        return goalMapper.toDtos(filteredGoals);
    }

    private List<GoalFilter> getApplicableGoalFilters(GoalFilterDto filter) {
        return filters.stream()
                .filter(f -> f.isApplicable(filter))
                .toList();
    }

    private void sentGoalAnalytic(Goal goal){
        goal.getUsers().forEach((user) -> {
            GoalCompletedEvent goalCompletedEvent = GoalCompletedEvent.builder()
                    .goalId(goal.getId())
                    .userId(user.getId())
                    .eventTime(LocalDateTime.now())
                    .build();
            goalMessagePublisher.publish(goalCompletedEvent);
        });
    }
}

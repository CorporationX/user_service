package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.GoalCompletedEvent;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.goal.GoalSetEvent;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.publisher.GoalCreateEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final SkillService skillService;
    private final GoalValidator goalValidator;
    private final UserService userService;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;
    private final UserContext userContext;
    private final GoalCreateEventPublisher goalCreateEventPublisher;

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filter) {
        Stream<Goal> foundGoals = goalRepository.findGoalsByUserId(userId);

        List<Goal> filteredGoals = filterGoals(foundGoals, filter).toList();

        for (Goal goal : filteredGoals) {
            goal.setSkillsToAchieve(skillService.findSkillsByGoalId(goal.getId()));
        }

        return filteredGoals.stream().map(goalMapper::toDto).toList();
    }

    public Stream<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filter) {
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                goals = goalFilter.filter(goals, filter);
            }
        }
        return goals;
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        Stream<Goal> foundGoals = goalRepository.findByParent(goalId);
        List<Goal> filteredGoals = filterGoals(foundGoals, filter).toList();

        for (Goal goal : filteredGoals) {
            goal.setSkillsToAchieve(skillService.findSkillsByGoalId(goal.getId()));
        }

        return filteredGoals.stream().map(goalMapper::toDto).toList();
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = findById(goalId);
        if (goalToUpdate.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Цель уже завершена");
        }
        Goal updatedGoal = goalMapper.updateGoal(goalToUpdate, goalDto);
        List<Skill> skills = goalDto.getSkillIds().stream().
                map(skillService::getSkillById)
                .toList();
        updatedGoal.setSkillsToAchieve(skills);
        Goal savedGoal = goalRepository.save(updatedGoal);
        if (savedGoal.getStatus() == GoalStatus.COMPLETED) {
            savedGoal.getUsers().forEach(user -> savedGoal.getSkillsToAchieve()
                    .forEach(skill -> skillService.assignSkillToUser(user.getId(), skill.getId())));
            GoalCompletedEvent goalCompletedEvent = GoalCompletedEvent.builder()
                    .userId(userContext.getUserId())
                    .goalId(goalId)
                    .goalCompletedAt(savedGoal.getUpdatedAt())
                    .build();
            goalCompletedEventPublisher.publish(goalCompletedEvent);
        }
        return goalMapper.toDto(savedGoal);
    }

    public Goal findById(long id) {
        return goalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Цель не найдена"));
    }

    @Transactional
    public GoalDto createGoal(long userId, GoalDto goalDto) {

        goalValidator.validate(userId, goalDto);

        Goal goalToSave = goalMapper.toEntity(goalDto);
        if (goalDto.getParentId() != null) {
            Goal parent = findById(goalDto.getParentId());
            goalToSave.setParent(parent);
        }

        if (goalDto.getSkillIds() != null && !goalDto.getSkillIds().isEmpty()) {
            List<Skill> goalSkills = new ArrayList<>();
            goalDto.getSkillIds().forEach(skillId ->
                    goalSkills.add(skillService.getSkillById(skillId)));
            goalToSave.setSkillsToAchieve(goalSkills);
        }

        User userToUpdate = userService.findById(userId);
        userToUpdate.getGoals().add(goalToSave);
        userService.saveUser(userToUpdate);
        goalToSave.setUsers(List.of(userToUpdate));

        Goal savedGoal = goalRepository.save(goalToSave);
        goalCreateEventPublisher.publish(new GoalSetEvent(userId, savedGoal.getId(), savedGoal.getUpdatedAt()));
        return goalMapper.toDto(savedGoal);
    }

    public int countActiveGoalsPerUser(long userId) {
        return goalRepository.countActiveGoalsPerUser(userId);
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }
}
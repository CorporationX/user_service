package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalSetEvent;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.publisher.GoalEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final SkillService skillService;
    private final GoalValidator goalValidator;
    private final UserService userService;
    private final GoalEventPublisher goalEventPublisher;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

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


    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {

        Goal foundGoal = findById(goalId);

        Goal goal = goalMapper.toEntity(goalDto);
        goal.setId(goalId);


        if (foundGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException("Цель уже завершена");
        }

        goalValidator.validateSkills(goal.getSkillsToAchieve());

        List<Skill> skillsToUpdate = goalDto.getSkillIds().stream().map(skillService::getSkillById).toList();
        goal.setSkillsToAchieve(skillsToUpdate);

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            goal.getUsers().forEach(user -> goalCompletedEventPublisher.publish(new GoalCompletedEvent(user.getId(), goalId, LocalDateTime.now())));

            goal.getUsers().forEach(user -> skillsToUpdate
                    .forEach(skill -> skillService.assignSkillToUser(user.getId(), skill.getId())));
        }


        return goalMapper.toDto(goalRepository.save(goal));
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

        goalEventPublisher.publish(new GoalSetEvent(userId, goalDto.getId(), LocalDateTime.now()));
        return goalMapper.toDto(goalRepository.save(goalToSave));
    }

    public int countActiveGoalsPerUser(long userId) {
        return goalRepository.countActiveGoalsPerUser(userId);
    }


    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }
}

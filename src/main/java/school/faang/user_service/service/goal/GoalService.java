package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> goalFilters;

    @Value("${app.user-service.goal-service.max-goals-amount}")
    private int maxGoalsAmount;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateUserExistence(userId);
        validateUserGoalsAmount(userId);
        validateSkillsExistence(goalDto.getSkillIds());

        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoalById(createdGoal.getId(), skillId));

        return goalMapper.entityToDto(createdGoal);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal foundGoal = getGoalById(goalId);
        validateGoalStatus(foundGoal);
        validateSkillsExistence(goalDto.getSkillIds());

        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            assignSkillsToUsers(goalRepository.findUsersByGoalId(foundGoal.getId()), goalDto.getSkillIds());
        }

        goalMapper.updateEntity(foundGoal, goalDto);
        foundGoal.setParent(goalRepository.findById(goalDto.getParentId()).orElse(null));
        foundGoal.setSkillsToAchieve(skillRepository.findAllById(goalDto.getSkillIds()));
        foundGoal.setUpdatedAt(LocalDateTime.now());
        goalRepository.save(foundGoal);

        return goalMapper.entityToDto(foundGoal);
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    public Stream<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findGoalsByUserId(userId);
    }

    public List<GoalDto> getGoalsByUserId(Long userId, GoalFilterDto filter) {
        Stream<Goal> goals = getGoalsByUserId(userId);
        List<Goal> filteredGoals = filterGoals(goals, filter).toList();

        return goalMapper.entityListToDtoList(filteredGoals);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters) {
        Stream<Goal> subtasks = goalRepository.findByParent(goalId);
        List<Goal> filteredGoals = filterGoals(subtasks, filters).toList();

        return goalMapper.entityListToDtoList(filteredGoals);
    }

    public Goal getGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.info("Goal with id '{}' does not exist", goalId);
                    return new EntityNotFoundException(String.format("Goal with id '%s' not found", goalId));
                });
    }

    private void assignSkillsToUsers(List<User> users, List<Long> skillIds) {
        users.forEach(user ->
                skillIds.forEach(skillId ->
                        skillRepository.assignSkillToUser(user.getId(), skillId)
                )
        );
    }

    private Stream<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filters) {
         return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .reduce(
                        goals,
                        (currentStream, goalFilter) -> goalFilter.apply(currentStream, filters),
                        (stream1, stream2) -> stream2
                );
    }

    private void validateUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id '{}' does not exist", userId);
            throw new EntityNotFoundException(String.format("User with id '%s' not found", userId));
        }
    }

    public void validateUserGoalsAmount(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) == maxGoalsAmount) {
            log.info("User with id '{}' already has max amount of active goals", userId);
            throw new IllegalStateException("User has max amount of active goals");
        }
    }

    private void validateSkillsExistence(List<Long> skillIds) {
        if (skillIds != null && skillRepository.countExisting(skillIds) != skillIds.size()) {
            log.info("Couldn't find some skills by ids '{}'", skillIds);
            throw new EntityNotFoundException("Skills with some ids not found");
        }
    }

    private void validateGoalStatus(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            log.info("Goal with id '{}' is already completed", goal.getId());
            throw new IllegalStateException("Goal is already completed");
        }
    }
}

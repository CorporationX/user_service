package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidation;

    public Goal findGoalById(Long id) {
        return goalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Goal not found by id: %s", id)));
    }

    /**
     * Creates a new goal with the given details for the given user ID.
     *
     * @param userId the ID of the user to create the goal for
     * @param goal the goal details
     * @return the newly created goal
     *
     * The validation of the goal request will fail if the request is invalid or
     * if the user does not exist. The goal will be created with the given details
     * and saved in the database.
     */
    public GoalDto createGoal(Long userId, GoalDto goal) {
        goalValidation.validateGoalRequest(userId, goal, true);

        goal.setId(null);
        goal.setStatus(GoalStatus.ACTIVE);

        Goal entity = goalMapper.toEntity(goal);

        Optional<User> user = userService.getUserById(userId);
        user.ifPresent(value -> entity.setUsers(List.of(value)));

        if (goal.getSkillIds() != null) {
            entity.setSkillsToAchieve(goal.getSkillIds().stream()
                    .map(skillService::getSkillById)
                    .toList());
        } else {
            entity.setSkillsToAchieve(Collections.emptyList());
        }

        goalRepository.save(entity);

        return goalMapper.toDto(entity);
    }

    /**
     * Updates an existing goal with the given details for the given user ID.
     *
     * @param userId the ID of the user to update the goal for
     * @param goal the goal details
     * @return the updated goal
     *
     * The validation of the goal request will fail if the request is invalid or
     * if the user does not exist. The goal will be updated with the given details
     * and saved in the database.
     */
    public GoalDto updateGoal(Long userId, GoalDto goal) {
        goalValidation.validateGoalRequest(userId, goal, false);

        Optional<Goal> optionalEntity = goalRepository.findById(goal.getId());

        if (optionalEntity.isEmpty()) {
            throw new EntityNotFoundException("Goal with id " + goal.getId() + " does not exist");
        }

        Goal entity = optionalEntity.get();

        entity.setTitle(goal.getTitle());
        entity.setDescription(goal.getDescription());
        entity.setStatus(goal.getStatus());
        entity.setDeadline(goal.getDeadline());

        if (goal.getMentorId() != null) {
            Optional<User> mentor = userService.getUserById(goal.getMentorId());
            mentor.ifPresent(entity::setMentor);
        }

        if (goal.getParentGoalId() != null) {
            entity.setParent(goalRepository.findById(goal.getParentGoalId()).get());
        }

        goalRepository.save(entity);

        return goalMapper.toDto(entity);
    }

    /**
     * Deletes a goal by its ID.
     *
     * @param goalId the ID of the goal to be deleted
     *
     * The deletion will fail if the goal does not exist.
     */
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    /**
     * Retrieves a list of goals for a given user ID, filtered by the provided goal filter.
     *
     * @param userId the ID of the user whose goals are to be retrieved
     * @param filters the goal filter to apply on the retrieved goals
     * @return a list of goals for the given user ID, filtered by the provided goal filter
     */
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId).stream();

        Stream<Goal> filteredGoals = goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        goals,
                        (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s1
                );

        return filteredGoals.map(goalMapper::toDto).toList();
    }
}

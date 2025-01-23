package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.GoalAlreadyCompletedException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.outbox.OutboxEventProcessor;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;

import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.utils.Helper;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private static final String AGGREGATE_TYPE = "Goal";
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidation;
    private final OutboxEventProcessor outboxEventProcessor;
    private final Helper helper;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    public Goal findGoalById(Long id) {
        return goalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Goal not found by id: %s", id)));
    }

    public void completeGoalAndPublishEvent(long goalId, long userId) {
        Goal entity = goalRepository.findByUserIdAndGoalId(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Goal with id %s not found for user %s", goalId, userId)));

        if (entity.getStatus() == GoalStatus.COMPLETED) {
            throw new GoalAlreadyCompletedException("Goal already completed");
        }

        entity.setStatus(GoalStatus.COMPLETED);
        goalRepository.save(entity);
        goalCompletedEventPublisher.publish(new GoalCompletedEvent(goalId, userId, LocalDateTime.now()));
    }



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

    public GoalDto updateGoal(Long userId, GoalDto goalDto) {
        goalValidation.validateGoalRequest(userId, goalDto, false);

        Optional<Goal> optionalEntity = goalRepository.findById(goalDto.getId());

        if (optionalEntity.isEmpty()) {
            throw new EntityNotFoundException("Goal with id " + goalDto.getId() + " does not exist");
        }

        Goal goal = optionalEntity.get();

        goal.setTitle(goalDto.getTitle());
        goal.setDescription(goalDto.getDescription());
        goal.setStatus(goalDto.getStatus());
        goal.setDeadline(goalDto.getDeadline());

        if (goalDto.getMentorId() != null) {
            Optional<User> mentor = userService.getUserById(goalDto.getMentorId());
            mentor.ifPresent(goal::setMentor);
        }

        if (goalDto.getParentGoalId() != null) {
            long parentGoalId = goalDto.getParentGoalId();
            Goal parentGoal = goalRepository.findById(parentGoalId).orElseThrow(() -> {
                log.info("Parent goal with id {} does not exist", goalDto.getParentGoalId());
                return new EntityNotFoundException(
                        "Parent goal with id " + goalDto.getParentGoalId() + " does not exist");
            });
            goal.setParent(parentGoal);
        }

        goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

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

    @Transactional
    public GoalDto completeTheGoal(long userId, long goalId) {
        Goal goalToComplete = getGoalToComplete(userId, goalId);
        if (isGoalStatusCompleted(goalToComplete)) {
            log.warn("User with id {} has already completed the goal with id {}", userId, goalId);
            return goalMapper.toDto(goalToComplete);
        } else {
            goalToComplete.setStatus(GoalStatus.COMPLETED);
            goalRepository.save(goalToComplete);
            GoalCompletedEvent goalCompletedEvent = new GoalCompletedEvent(userId, goalId, LocalDateTime.now());

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(goalId)
                    .aggregateType(AGGREGATE_TYPE)
                    .eventType(GoalCompletedEvent.class.getSimpleName())
                    .payload(helper.serializeToJson(goalCompletedEvent))
                    .createdAt(goalCompletedEvent.completedAt())
                    .processed(false)
                    .build();

            outboxEventProcessor.saveOutboxEvent(outboxEvent);
        }
        return goalMapper.toDto(goalToComplete);
    }

    private Goal getGoalToComplete(long userId, long goalId) {
        return goalRepository.findByUserIdAndGoalId(userId, goalId).orElseThrow(() -> {
            log.error("User with id {} does not have a goal with id {}", userId, goalId);
            return new EntityNotFoundException("User with id " + userId + " does not have a goal with id " + goalId);
        });
    }

    private boolean isGoalStatusCompleted(Goal goal) {
        return goal.getStatus() == GoalStatus.COMPLETED;
    }
}

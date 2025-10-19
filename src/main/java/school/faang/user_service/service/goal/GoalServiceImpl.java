package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {

    @Value("${goal.active.amount}")
    private int maxActiveGoals;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final List<GoalFilter> goalFilters;

    @Override
    @Transactional
    public GoalDto create(CreateGoalDto createGoalDto) {
        Goal goal = goalMapper.toGoal(createGoalDto);
        List<User> users = userRepository.findAllById(createGoalDto.userIds());
        validateAllUsersExist(users, createGoalDto.userIds());
        goal.setUsers(new ArrayList<>(users));
        long currentUserId = userContext.getUserId();
        log.info("Check conditions to create the goal '{}'", createGoalDto.title());
        if (createGoalDto.mentorId() == null && !createGoalDto.userIds().contains(currentUserId)) {
            log.error("The person who is trying to create the goal '{}' is an unknown user", createGoalDto.title());
            throw new ForbiddenException("The goal can be created by either mentor for mentee or user for yourself");
        }
        if (createGoalDto.mentorId() == null && createGoalDto.userIds().contains(currentUserId)) {
            log.info("The person who is trying to create the goal '{}' is User #{}",
                    createGoalDto.title(), currentUserId);
            checkCountUsersActiveGoals(createGoalDto.title(), currentUserId);
        }
        if (createGoalDto.mentorId() != null) {
            log.info("The person who is trying to create the goal '{}' is Mentor #{}",
                    createGoalDto.title(), createGoalDto.mentorId());
            goal.setMentor(userRepository.getByIdOrThrow(createGoalDto.mentorId()));
            for (User user : goal.getUsers()) {
                log.info("Count active goals for User #{}", user.getId());
                checkCountUsersActiveGoals(createGoalDto.title(), user.getId());
            }
        }
        goal = goalRepository.save(goal);
        log.info("The goal '{}' is created. The goal has got ID={}", goal.getTitle(), goal.getId());
        return goalMapper.toGoalDto(goal);
    }

    @Override
    @Transactional
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        Goal currentGoal = goalRepository.getByIdOrThrow(goalId);
        log.info("Check conditions to update the goal #{}", goalId);
        if (currentGoal.getStatus() == GoalStatus.COMPLETED) {
            log.error("The goal #{} has status Completed", goalId);
            throw new ForbiddenException("Unable to update completed goal");
        }
        long currentUserId = userContext.getUserId();
        if (currentGoal.getMentor() != null
                && updateGoalDto.status() == GoalStatus.COMPLETED
                && currentGoal.getMentor().getId() != currentUserId) {
            log.error("The goal #{} has a mentor. The person who is trying to complete the goal is not mentor", goalId);
            throw new ForbiddenException("The goal can be completed by mentor only");
        }
        if (!hasAccessToAct(currentGoal, userRepository.getByIdOrThrow(currentUserId))) {
            log.error("The person who is trying to update the goal #{} is an unknown user", goalId);
            throw new ForbiddenException("The goal can be updated by either mentor or goal participant");
        }
        goalMapper.update(currentGoal, updateGoalDto);
        log.info("The goal #{} is updated", goalId);
        return goalMapper.toGoalDto(currentGoal);
    }

    @Override
    @Transactional
    public void delete(long goalId) {
        Goal currentGoal = goalRepository.getByIdOrThrow(goalId);
        long currentUserId = userContext.getUserId();
        User currentUser = userRepository.getByIdOrThrow(currentUserId);
        log.info("Check conditions to delete the goal #{}", goalId);
        if (!hasAccessToAct(currentGoal, currentUser)) {
            log.error("The person who is trying to delete the goal #{} is an unknown user", goalId);
            throw new ForbiddenException("The goal can be deleted by either mentor or goal participant");
        }
        if (isCurrentMentor(currentGoal, currentUser)) {
            goalRepository.delete(currentGoal);
            log.info("Mentor deleted the goal #{} from the mentees", goalId);
            return;
        }
        if (currentGoal.getUsers().contains(currentUser)) {
            currentGoal.getUsers().remove(currentUser);
            log.info("User #{} no longer has the goal #{}", currentUserId, goalId);
        }
        if (currentGoal.getUsers().isEmpty()) {
            goalRepository.delete(currentGoal);
            log.info("No other user has the goal #{}. The goal is deleted", goalId);
        }
    }

    @Override
    @Transactional
    public List<GoalDto> getByFilters(GoalFilterDto goalFilterDto) {
        log.info("User #{} is trying to apply filters", userContext.getUserId());
        Stream<Goal> filteredGoals = goalRepository.findAll().stream();
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(goalFilterDto)) {
                filteredGoals = goalFilter.apply(filteredGoals, goalFilterDto);
            }
        }
        log.info("Goals are filtered for User #{}", userContext.getUserId());
        return filteredGoals
                .map(goalMapper::toGoalDto)
                .toList();
    }

    private void validateAllUsersExist(List<User> users, List<Long> userIds) {
        if (users.size() != userIds.size()) {
            List<Long> foundUserIds = users.stream()
                    .map(User::getId)
                    .toList();
            userIds.stream()
                    .filter(id -> !foundUserIds.contains(id))
                    .forEach(id -> log.error("User #{} is not found", id));
            throw new EntityNotFoundException("Could not find all users");
        }

    }

    private void checkCountUsersActiveGoals(String title, long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) < maxActiveGoals) {
            log.info("The goal '{}' is added to User #{}", title, userId);
        } else {
            log.error("User #{} has either {} or more active goals", userId, maxActiveGoals);
            throw new DataValidationException(
                    String.format("Unable to create more than %d goals per user", maxActiveGoals));
        }
    }

    private boolean isCurrentMentor(Goal currentGoal, User currentUser) {
        return currentGoal.getMentor() != null && currentGoal.getMentor().equals(currentUser);
    }

    private boolean hasAccessToAct(Goal currentGoal, User currentUser) {
        return currentGoal.getUsers().contains(currentUser) || isCurrentMentor(currentGoal, currentUser);
    }
}

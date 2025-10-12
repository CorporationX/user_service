package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
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
    private static final String SUCCESSFULLY_LOG = "Successfully";

    @Value("${goal.active.amount}")
    private int activeGoals;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final List<GoalFilter> goalFilters;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        log.info("Try to create a new goal in the Service");
        long currentUserId = userContext.getUserId();
        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setUsers(new ArrayList<>());
        for (Long userId : createGoalDto.userIds()) {
            goal.getUsers().add(userRepository.getByIdOrThrow(userId));
        }
        log.info("Check conditions to create");
        if (createGoalDto.mentorId() != null) {
            log.info("The person who is trying to create the goal is Mentor");
            goal.setMentor(userRepository.getByIdOrThrow(createGoalDto.mentorId()));
            boolean isExceptionOccured = false;
            for (User user : goal.getUsers()) {
                log.info("Count active goals for user #{}", user.getId());
                if (goalRepository.countActiveGoalsPerUser(user.getId()) < activeGoals) {
                    log.info("The goal is added to user #{}", user.getId());
                } else {
                    log.error("User #{} has either {} or more active goals", user.getId(), activeGoals);
                    isExceptionOccured = true;
                    break;
                }
            }
            if (isExceptionOccured) {
                throw new DataValidationException(
                        String.format("Unable to create more than %d goals per user", activeGoals));
            }
        } else if (createGoalDto.userIds().contains(currentUserId)) {
            log.info("The person who is trying to create the goal is User. Goal will be created for this User");
            if (goalRepository.countActiveGoalsPerUser(currentUserId) < activeGoals) {
                log.info("The goal is added to user #{}", currentUserId);
            } else {
                log.error("User #{} has either {} or more active goals", currentUserId, activeGoals);
                throw new DataValidationException(
                        String.format("Unable to create more than %d goals per user", activeGoals));
            }
        } else {
            log.error("The person who is trying to create the goal is an unknown user");
            throw new ForbiddenException("The goal can be created by either mentor for mentee or user for yourself");
        }
        log.info(SUCCESSFULLY_LOG);
        goal = goalRepository.save(goal);
        log.info("The goal is created");
        return goalMapper.toGoalDto(goal);
    }

    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        log.info("Try to update a goal in the Service");
        Goal currentGoal = goalRepository.getByIdOrThrow(goalId);
        log.info("Check conditions to update");
        log.info("Check the goal`s status");
        if (currentGoal.getStatus() == GoalStatus.COMPLETED) {
            log.error("The goal #{} has status Completed", goalId);
            throw new ForbiddenException("Unable to update completed goal");
        }
        log.info("Check who is trying to update the goal");
        if (currentGoal.getMentor() != null
                && updateGoalDto.status() == GoalStatus.COMPLETED
                && !currentGoal.getMentor().getId().equals(userContext.getUserId())) {
            log.error("The goal #{} has mentor. The person who is trying to complete the goal is not mentor", goalId);
            throw new ForbiddenException("The goal can be completed by mentor only");
        }
        if (!currentGoal.getUsers().contains(userRepository.getByIdOrThrow(userContext.getUserId()))
                && (currentGoal.getMentor() == null || userContext.getUserId() != currentGoal.getMentor().getId())) {
            log.error("The person who is trying to update the goal #{} is an unknown user", goalId);
            throw new ForbiddenException("The goal can be updated by either mentor or goal participant");
        }
        log.info(SUCCESSFULLY_LOG);
        goalMapper.update(goalRepository.getByIdOrThrow(goalId), updateGoalDto);
        log.info("The goal #{} is updated", goalId);
        return goalMapper.toGoalDto(goalRepository.getByIdOrThrow(goalId));
    }

    @Override
    public void delete(long goalId) {
        log.info("Try to delete a goal in the Service");
        Goal currentGoal = goalRepository.getByIdOrThrow(goalId);
        log.info("Check who is trying to delete the goal #{}", goalId);
        if (!currentGoal.getUsers().contains(userRepository.getByIdOrThrow(userContext.getUserId()))
                && (currentGoal.getMentor() == null || userContext.getUserId() != currentGoal.getMentor().getId())) {
            log.error("The person who is trying to delete the goal #{} is an unknown user", goalId);
            throw new ForbiddenException("The goal can be deleted by either mentor or goal participant");
        }
        if (currentGoal.getMentor() != null
                && currentGoal.getMentor().equals(userRepository.getByIdOrThrow(userContext.getUserId()))) {
            goalRepository.delete(goalRepository.getByIdOrThrow(goalId));
            log.info("Mentor deleted the goal #{} from the mentees", goalId);
        } else {
            goalRepository.getByIdOrThrow(goalId).getUsers()
                    .remove(userRepository.getByIdOrThrow(userContext.getUserId()));
            log.info("User #{} no longer has the goal #{}", userContext.getUserId(), goalId);
            if (goalRepository.getByIdOrThrow(goalId).getUsers().isEmpty()) {
                goalRepository.delete(goalRepository.getByIdOrThrow(goalId));
                log.info("No other user has the goal #{}. The goal is deleted", goalId);
            }
        }
    }

    @Override
    public List<GoalDto> getByFilters(GoalFilterDto goalFilterDto) {
        log.info("Try to filter goals in the Service");
        Stream<Goal> filteredGoals = goalRepository.findAll().stream();
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(goalFilterDto)) {
                filteredGoals = goalFilter.apply(filteredGoals, goalFilterDto);
            }
        }
        log.info(SUCCESSFULLY_LOG);
        return filteredGoals
                .map(goalMapper::toGoalDto)
                .toList();
    }
}

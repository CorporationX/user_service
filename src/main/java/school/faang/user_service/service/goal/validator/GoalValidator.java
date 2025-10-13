package school.faang.user_service.service.goal.validator;

import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;
import java.util.Objects;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;


public class GoalValidator {
    private static final int MAX_GOALS_FOR_ONE_USER = 2;

    public static void validateUserAccess(List<Long> userIds, Long mentorId, Long userContextId) {
        if ((userIds == null || !userIds.contains(userContextId))
                && !Objects.equals(mentorId, userContextId)) {
            throw new ForbiddenException("The user is not on the participation list or among mentors");
        }
    }

    public static void validateMentorOrUsersPresence(List<Long> userIds, Long mentorId) {
        if (userIds == null && mentorId == null) {
            throw new DataValidationException("the goal must have either a mentor or users");
        }
    }

    public static void validateGoalStatusTransition(Goal goal, GoalStatus goalStatus, Long userId) {

        if (Objects.equals(goal.getStatus(), COMPLETED)) {
            throw new ForbiddenException(String.format("Goal title - %s, goal id - %d has already been completed",
                    goal.getTitle(), goal.getId()));
        }

        if (Objects.equals(goalStatus, COMPLETED)) {
            boolean hasMentor = goal.getMentor() != null && goal.getMentor().getId() != null;
            boolean isMentor = hasMentor && Objects.equals(userId, goal.getMentor().getId());
            boolean isAssignedUser = !hasMentor
                    && goal.getUsers() != null
                    && goal.getUsers().stream()
                            .filter(Objects::nonNull)
                            .anyMatch(user -> Objects.equals(user.getId(), userId));
            if (!isMentor && !isAssignedUser) {
                throw new ForbiddenException(
                        String.format("Only a mentor or assigned user can complete the goal (title - %s, goal id - %d)",
                        goal.getTitle(), goal.getId()));
            }
        }

    }

    public static void validateUserAccessToGoal(long mentorId, Goal goal, Long userId) {

        List<Long> usersIdByGoal = goal.getUsers().stream()
                .map(User::getId)
                .toList();

        if (!Objects.equals(userId, mentorId) && !usersIdByGoal.contains(userId)) {
            throw new ForbiddenException("The user with ID is not a mentor or participant of the Goal");
        }
    }

    public static void validateUserGoalsLimit(List<User> userList) {

        userList.forEach(user -> {
            if (user.getGoals() != null) {
                long totalGoal = user.getGoals().stream()
                        .filter(g -> Objects.equals(g.getStatus(), ACTIVE))
                        .count();
                if (totalGoal >= MAX_GOALS_FOR_ONE_USER) {
                    throw new DataValidationException(String.format("The user {} already has {} goals",
                            user.getId(), MAX_GOALS_FOR_ONE_USER));
                }
            }
        });
    }
}

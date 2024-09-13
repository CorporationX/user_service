package school.faang.user_service.util.goal.invitation;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class InvitationFabric {
    public static Stream<GoalInvitation> getInvitations(int numberOfInvitations) {
        return LongStream
                .rangeClosed(1, numberOfInvitations)
                .mapToObj(i -> getInvitation(i, i));
    }

    public static GoalInvitation getInvitation(Long id, Long inviterId, Long invitedUserId, Long goalId, RequestStatus status) {
        return GoalInvitation.builder()
                .id(id)
                .inviter(getUser(inviterId))
                .invited(getUser(invitedUserId))
                .goal(getGoal(goalId))
                .status(status)
                .build();
    }

    public static GoalInvitation getInvitation(Long id, User inviter, User invitedUser, Goal goal) {
        return GoalInvitation.builder()
                .id(id)
                .inviter(inviter)
                .invited(invitedUser)
                .goal(goal)
                .build();
    }

    public static GoalInvitation getInvitation(Long inviterId, Long invitedId) {
        return GoalInvitation
                .builder()
                .inviter(getUser(inviterId))
                .invited(getUser(invitedId))
                .build();
    }

    public static GoalInvitation getInvitation(String inviterName, String invitedName) {
        return GoalInvitation
                .builder()
                .inviter(getUser(inviterName))
                .invited(getUser(invitedName))
                .build();
    }

    public static GoalInvitation getInvitation(RequestStatus status) {
        return GoalInvitation
                .builder()
                .status(status)
                .build();
    }

    public static User getUser(Long id, List<Goal> goals) {
        return User.builder()
                .id(id)
                .goals(goals)
                .build();
    }

    public static User getUser(Long id) {
        return User
                .builder()
                .id(id)
                .build();
    }

    public static User getUser(String name) {
        return User
                .builder()
                .username(name)
                .build();
    }

    public static List<Goal> getGoals(int numberOfGoals) {
        return LongStream
                .rangeClosed(1, numberOfGoals)
                .mapToObj(InvitationFabric::getGoal)
                .toList();
    }

    public static Goal getGoal(Long id) {
        return Goal.builder()
                .id(id)
                .status(GoalStatus.ACTIVE)
                .build();
    }

    public static GoalInvitationDto getInvitationDto(Long id, Long inviterId, Long invitedUserId,
                                                     Long goalId, RequestStatus status) {
        return GoalInvitationDto
                .builder()
                .id(id)
                .inviterId(inviterId)
                .invitedUserId(invitedUserId)
                .goalId(goalId)
                .status(status)
                .build();
    }

    public static InvitationFilterDto getInvitationFilterDto(String inviterNamePattern, String invitedNamePattern,
                                                             Long inviterId, Long invitedId, RequestStatus status) {
        return InvitationFilterDto
                .builder()
                .inviterNamePattern(inviterNamePattern)
                .invitedNamePattern(invitedNamePattern)
                .inviterId(inviterId)
                .invitedId(invitedId)
                .status(status)
                .build();
    }

    public static InvitationFilterDto getInvitationFilterDto(String inviterNamePattern, String invitedNamePattern) {
        return InvitationFilterDto
                .builder()
                .inviterNamePattern(inviterNamePattern)
                .invitedNamePattern(invitedNamePattern)
                .build();
    }

    public static InvitationFilterDto getInvitationFilterDto(Long inviterId, Long invitedUserId) {
        return InvitationFilterDto
                .builder()
                .inviterId(inviterId)
                .invitedId(invitedUserId)
                .build();
    }

    public static InvitationFilterDto getInvitationFilterDto(RequestStatus status) {
        return InvitationFilterDto
                .builder()
                .status(status)
                .build();
    }

    public static InvitationFilterDto getInvitationFilterDto() {
        return InvitationFilterDto
                .builder()
                .build();
    }
}

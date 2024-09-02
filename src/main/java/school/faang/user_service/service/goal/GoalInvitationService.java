package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.invitation.InvitationCheckException;
import school.faang.user_service.exception.goal.invitation.InvitationEntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilter;

import java.util.List;

import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.GOAL_INVITATION_NOT_FOUND;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.GOAL_NOT_FOUND_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.INVITED_USER_NOT_FOUND_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.INVITER_NOT_FOUND_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.USERS_SAME_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.USER_ALREADY_HAS_GOAL;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.USER_GOALS_LIMIT_EXCEEDED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    public static final int USER_GOALS_LIMIT = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final List<InvitationFilter> filters;

    @Transactional
    public GoalInvitation createInvitation(GoalInvitation invitation, long inviterId, long invitedUserId, long goalId) {
        log.info("Create invitation, inviterId: {}, invitedUserId: {}, goalId: {}", inviterId, invitedUserId, goalId);
        checkUsersForMatching(inviterId, invitedUserId);
        invitationSetEntities(invitation, inviterId, invitedUserId, goalId);
        return goalInvitationRepository.save(invitation);
    }

    private void checkUsersForMatching(long inviterId, long invitedUserId) {
        log.info("Check users for matching, inviterId: {}, invitedUserId: {}", inviterId, invitedUserId);
        if (inviterId == invitedUserId) {
            throw new InvitationCheckException(USERS_SAME_MESSAGE_FORMAT, inviterId, invitedUserId);
        }
    }

    private void invitationSetEntities(GoalInvitation invitationEntity, long inviterId, long invitedUserId, long goalId) {
        log.info("Set entities to new invitation");
        log.info("Find inviter user with id: {}", inviterId);
        invitationEntity.setInviter(userRepository.findById(inviterId).orElseThrow(() ->
                new InvitationEntityNotFoundException(INVITER_NOT_FOUND_MESSAGE_FORMAT, inviterId)));
        log.info("Find invited user with id: {}", invitedUserId);
        invitationEntity.setInvited(userRepository.findById(invitedUserId).orElseThrow(() ->
                new InvitationEntityNotFoundException(INVITED_USER_NOT_FOUND_MESSAGE_FORMAT, invitedUserId)));
        log.info("Find goal with id: {}", goalId);
        invitationEntity.setGoal(goalRepository.findById(goalId).orElseThrow(() ->
                new InvitationEntityNotFoundException(GOAL_NOT_FOUND_MESSAGE_FORMAT, goalId)));
    }

    @Transactional
    public void acceptGoalInvitation(long id) {
        log.info("Accept goal invitation with id: {}", id);
        var invitation = findGoalInvitationById(id);
        var invitedUser = invitation.getInvited();
        var goal = invitation.getGoal();
        checkUserGoals(invitedUser, goal, invitation);
        goal.getUsers().add(invitedUser);
        invitation.setStatus(RequestStatus.ACCEPTED);
    }

    private void checkUserGoals(User invitedUser, Goal goal, GoalInvitation invitation) {
        log.info("Check for user ability with id: {} get goal with id: {}", invitedUser.getId(), goal.getId());
        var activeGoals = getActiveGoals(invitedUser);
        if (activeGoals.contains(goal)) {
            invitation.setStatus(RequestStatus.REJECTED);
            throw new InvitationCheckException(USER_ALREADY_HAS_GOAL, invitedUser.getId(), goal.getId());
        }
        if (activeGoals.size() >= USER_GOALS_LIMIT) {
            invitation.setStatus(RequestStatus.REJECTED);
            throw new InvitationCheckException(USER_GOALS_LIMIT_EXCEEDED, invitedUser.getId(), USER_GOALS_LIMIT);
        }
    }

    private List<Goal> getActiveGoals(User invitedUser) {
        log.info("Get active goal of user with id: {}", invitedUser.getId());
        return invitedUser.getGoals()
                .stream()
                .filter(g -> g.getStatus().equals(GoalStatus.ACTIVE))
                .toList();
    }

    @Transactional
    public void rejectGoalInvitation(long id) {
        log.info("Reject goal with id: {}", id);
        var invitation = findGoalInvitationById(id);
        invitation.setStatus(RequestStatus.REJECTED);
    }

    private GoalInvitation findGoalInvitationById(long id) {
        log.info("Find invitation with id: {}", id);
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new InvitationEntityNotFoundException(GOAL_INVITATION_NOT_FOUND, id));
    }

    @Transactional(readOnly = true)
    public List<GoalInvitation> getInvitations(InvitationFilterDto filterDto) {
        log.info("Get invitations by filter: {}", filterDto);
        var invitations = goalInvitationRepository.findAll();
        return filters
                .stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(invitations.stream(),
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();
    }
}

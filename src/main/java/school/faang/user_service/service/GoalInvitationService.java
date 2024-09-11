package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import static school.faang.user_service.repository.goal.specification.GoalInvitationSpecification.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;

    public static final String GOAL_MISSING = "Goal wasn't found";
    public static final String INVITATION_MISSING = "Invitation wasn't found";
    public static final String INVITER_INVITED_SAME = "Inviter and invited are the same user";
    public static final String INVITER_INVITED_MISSING = "Inviter or/and invited not found";
    public static final String INVITED_ALREADY_WORKING_ON_GOAL = "Invited user is already working on this goal";
    public static final String INVITED_MAX_ACTIVE_GOALS = "Invited user reached max active goals: " + MAX_ACTIVE_GOALS;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalInvitation createInvitation(GoalInvitation invitation) {
        Long inviterId = invitation.getInviter().getId();
        Long invitedId = invitation.getInvited().getId();
        Goal target = invitation.getGoal();

        if (!goalRepository.existsById(target.getId())) {
            log.error("{} [goalId: {}]", GOAL_MISSING, target.getId());
            throw new IllegalStateException(GOAL_MISSING);
        }

        if (inviterId.equals(invitedId)) {
            log.error("{} [inviterId: {}]", INVITER_INVITED_SAME, inviterId);
            throw new IllegalArgumentException(INVITER_INVITED_SAME);
        }

        if (userRepository.findAllById(List.of(inviterId, invitedId)).size() != 2) {
            log.error("{} [inviterId: {}, invitedUserId: {}]", INVITER_INVITED_MISSING, inviterId, invitedId);
            throw new IllegalStateException(INVITER_INVITED_MISSING);
        }

        User inviter = userRepository.findById(inviterId).get();
        User invited = userRepository.findById(invitedId).get();

        GoalInvitation saved = goalInvitationRepository.save(invitation);
        log.info("Created goal invitation (id: {}, status: {})", saved.getId(), saved.getStatus());

        inviter.getSentGoalInvitations().add(saved);
        invited.getReceivedGoalInvitations().add(saved);

        User inviterUpdated = userRepository.save(inviter);
        User invitedUpdated = userRepository.save(invited);

        log.info("Added invitation to inviter user's(id: {}) sent invitations: {}", inviterId, inviterUpdated.getSentGoalInvitations().stream()
                .map(GoalInvitation::getId)
                .toList());
        log.info("Added invitation to invited user's(id: {}) received invitations: {}", invitedId, invitedUpdated.getReceivedGoalInvitations().stream()
                .map(GoalInvitation::getId)
                .toList());

        return saved;
    }

    public void acceptGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> {
                    log.error("{} [invitationId: {}]", INVITATION_MISSING, id);
                    return new IllegalArgumentException(INVITATION_MISSING);
                }
        );
        Goal target = invitation.getGoal();
        User invited = invitation.getInvited();

        if (!goalRepository.existsById(target.getId())) {
            log.error("{} [goalId: {}]", GOAL_MISSING, target.getId());
            throw new IllegalStateException(GOAL_MISSING);
        }

        if (invited.getGoals().contains(target)) {
            log.error("{} [goalId: {}]", INVITED_ALREADY_WORKING_ON_GOAL, target.getId());
            throw new IllegalStateException(INVITED_ALREADY_WORKING_ON_GOAL);
        }

        if (goalRepository.countActiveGoalsPerUser(invited.getId()) >= MAX_ACTIVE_GOALS) {
            log.error("{} [invitedId: {}]", INVITED_MAX_ACTIVE_GOALS, invited.getId());
            throw new IllegalStateException(INVITED_MAX_ACTIVE_GOALS);
        }

        target.getUsers().add(invited);
        goalRepository.save(target);

        invited.getGoals().add(target);
        User withAddedGoal = userRepository.save(invited);
        log.info("Invited user's goals updated: [goalIds: {}]", withAddedGoal.getGoals().stream().map(Goal::getId).toList());

        invitation.setStatus(RequestStatus.ACCEPTED);
        GoalInvitation accepted = goalInvitationRepository.save(invitation);
        log.info("Successfully accepted goal invitation: (id: {}, status: {})", id, accepted.getStatus());
    }

    public void rejectGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> {
                    log.error("{} [invitationId: {}]", INVITATION_MISSING, id);
                    return new IllegalArgumentException(INVITATION_MISSING);
                }
        );

        if (!goalRepository.existsById(invitation.getGoal().getId())) {
            log.error("{} [goalId: {}]", GOAL_MISSING, invitation.getGoal().getId());
            throw new IllegalStateException(GOAL_MISSING);
        }

        invitation.setStatus(RequestStatus.REJECTED);
        invitation = goalInvitationRepository.save(invitation);
        log.info("Successfully rejected goal invitation (id: {}, status: {})", id, invitation.getStatus());
    }

    public List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> filteredInvitations = goalInvitationRepository.findAll();
        if (Objects.isNull(filter)) {
            return filteredInvitations;
        }

        Long inviterIdFilter = filter.getInviterId();
        Long invitedIdFilter = filter.getInvitedId();
        String inviterNameFilter = filter.getInviterNamePattern();
        String invitedNameFilter = filter.getInvitedNamePattern();
        RequestStatus statusFilter = filter.getStatus();

        if (anyNull(filter.getInviterNamePattern(), filter.getInvitedNamePattern())) {
            filteredInvitations = filteredInvitations.stream()
                    .filter(invitation -> Objects.isNull(inviterIdFilter) || Objects.equals(invitation.getInviter().getId(), inviterIdFilter))
                    .filter(invitation -> Objects.isNull(invitedIdFilter) || Objects.equals(invitation.getInvited().getId(), invitedIdFilter))
                    .filter(invitation -> Objects.isNull(statusFilter) || Objects.equals(invitation.getStatus(), statusFilter))
                    .toList();
        } else if (anyNull(filter.getInviterId(), filter.getInvitedId())) {
            filteredInvitations = filteredInvitations.stream()
                    .filter(invitation -> Objects.isNull(inviterNameFilter) || invitation.getInviter().getUsername().contains(inviterNameFilter))
                    .filter(invitation -> Objects.isNull(invitedNameFilter) || invitation.getInvited().getUsername().contains(invitedNameFilter))
                    .filter(invitation -> Objects.equals(invitation.getStatus(), statusFilter))
                    .toList();
        }

        return filteredInvitations;
    }

    public List<GoalInvitation> getInvitationsBySpec(InvitationFilterDto filter) {
        if (Objects.isNull(filter)) {
            return goalInvitationRepository.findAll();
        }

        Specification<GoalInvitation> spec = Specification.where(null);
        spec = spec
                .and(hasInviterId(filter.getInviterId()))
                .and(hasInvitedId(filter.getInvitedId()))
                .and(hasInviterNamePattern(filter.getInviterNamePattern()))
                .and(hasInvitedNamePattern(filter.getInvitedNamePattern()))
                .and(hasStatus(filter.getStatus()));

        return goalInvitationRepository.findAll(spec);
    }

    private boolean anyNull(Object... filterParams) {
        return Arrays.stream(filterParams).anyMatch(Objects::isNull);
    }
}

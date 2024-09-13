package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import java.util.List;
import java.util.Objects;
import static school.faang.user_service.service.util.GoalInvitationUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        if (Objects.isNull(filter)) {
            return goalInvitationRepository.findAll();
        }

        return goalInvitationRepository.getAllFiltered(
                filter.getInviterId(),
                filter.getInvitedId(),
                filter.getInviterNamePattern(),
                filter.getInvitedNamePattern(),
                filter.getStatus().ordinal()
        );
    }

    @Transactional
    public GoalInvitation createInvitation(GoalInvitation invitation) {
        User inviter = userRepository.findById(invitation.getInviter().getId()).orElseThrow();
        User invited = userRepository.findById(invitation.getInvited().getId()).orElseThrow();

        validateGoalExists(invitation.getGoal().getId());
        validateInviterInvitedDistinct(inviter.getId(), invited.getId());

        GoalInvitation saved = goalInvitationRepository.save(invitation);
        inviter.getSentGoalInvitations().add(saved);
        invited.getReceivedGoalInvitations().add(saved);
        userRepository.saveAll(List.of(inviter, invited));
        log.info("Created goal invitation (id: {}, status: {})", saved.getId(), saved.getStatus());

        return saved;
    }

    @Transactional
    public void acceptGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(INVITATION_MISSING));
        Goal target = invitation.getGoal();
        User invited = invitation.getInvited();

        validateGoalExists(target.getId());
        validateInvitedNotWorkingOnGoal(invited, target);
        validateInvitedActiveGoalsCount(invited.getId());

        target.getUsers().add(invited);
        goalRepository.save(target);

        invited.getGoals().add(target);
        userRepository.save(invited);

        invitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(invitation);
        log.info("Successfully accepted goal invitation: (goalId: {}, status: {}, userId: {})", id, invitation.getStatus(), invited.getId());
    }

    public void rejectGoalInvitation(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(INVITATION_MISSING));
        validateGoalExists(invitation.getGoal().getId());

        invitation.setStatus(RequestStatus.REJECTED);
        GoalInvitation updated = goalInvitationRepository.save(invitation);
        log.info("Successfully rejected goal invitation (id: {}, status: {})", id, updated.getStatus());
    }

    private void validateGoalExists(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            log.error("{} [goalId: {}]", GOAL_MISSING, goalId);
            throw new IllegalStateException(GOAL_MISSING);
        }
    }

    private void validateInviterInvitedDistinct(Long inviterId, Long invitedId) {
        if (Objects.equals(inviterId, invitedId)) {
            log.error("{} [inviterId: {}]", INVITER_INVITED_SAME, inviterId);
            throw new IllegalArgumentException(INVITER_INVITED_SAME);
        }
    }

    private void validateInvitedActiveGoalsCount(Long invitedId) {
        if (goalRepository.countActiveGoalsPerUser(invitedId) >= MAX_ACTIVE_GOALS) {
            log.error("{} [invitedId: {}]", INVITED_MAX_ACTIVE_GOALS, invitedId);
            throw new IllegalStateException(INVITED_MAX_ACTIVE_GOALS);
        }
    }

    private void validateInvitedNotWorkingOnGoal(User invited, Goal toCheck) {
        if (invited.getGoals().contains(toCheck)) {
            log.error("{} [goalId: {}]", INVITED_ALREADY_WORKING_ON_GOAL, toCheck.getId());
            throw new IllegalStateException(INVITED_ALREADY_WORKING_ON_GOAL);
        }
    }
}

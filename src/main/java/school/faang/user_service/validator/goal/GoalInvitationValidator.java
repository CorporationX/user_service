package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {
    private static final int MAX_SET_GOALS = 3;
    private static final String MISSING_GOAL = "Goal needs to be registered in the system.";
    private static final String MISSING_INVITATION = "Invitation has to be registered in the system.";
    private static final String SAME_INVITER_INVITED = "Inviter and invited users can't be the same user.";
    private static final String INVITER_INVITED_NOT_REGISTERED = "Both inviter and invited users need to be registered in the system.";
    private static final String INVITED_ALREADY_WORKING_ON_GOAL = "Invited user already working on this goal.";
    private static final String INVITED_REACHED_MAX_GOALS = "Inviter can't have more than " + MAX_SET_GOALS + " active goals.";

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public void validateNewInvitation(GoalInvitationDto dto) {
        Long inviterId = dto.getInviterId();
        Long invitedId = dto.getInvitedUserId();

        if (!goalRepository.existsById(dto.getGoalId())) {
            throw new IllegalStateException(MISSING_GOAL);
        }

        if (inviterId.equals(invitedId)) {
            throw new IllegalArgumentException(SAME_INVITER_INVITED);
        }

        if (userRepository.findAllById(List.of(inviterId, invitedId)).size() != 2) {
            throw new IllegalStateException(INVITER_INVITED_NOT_REGISTERED);
        }
    }

    public GoalInvitation validatedForAccepting(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(MISSING_INVITATION)
        );
        Goal target = invitation.getGoal();
        User invited = invitation.getInvited();

        if (!goalRepository.existsById(target.getId())) {
            throw new IllegalStateException(MISSING_GOAL);
        }

        if (invited.getGoals().contains(target)) {
            throw new IllegalStateException(INVITED_ALREADY_WORKING_ON_GOAL);
        }

        if (goalRepository.countActiveGoalsPerUser(invited.getId()) > MAX_SET_GOALS) {
            throw new IllegalStateException(INVITED_REACHED_MAX_GOALS);
        }

        return invitation;
    }

    public GoalInvitation validatedForRejecting(long id) {
        GoalInvitation invitation = goalInvitationRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(MISSING_INVITATION)
        );

        if (!goalRepository.existsById(invitation.getGoal().getId())) {
            throw new IllegalStateException(MISSING_GOAL);
        }

        return invitation;
    }
}

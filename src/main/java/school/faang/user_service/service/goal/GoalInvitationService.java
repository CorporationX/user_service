package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static int GOALS_MAX_NUM = 3;
    private final GoalInvitationRepository goalInvitationRepository;

    private final GoalInvitationMapper goalInvitationMapper;

    private final UserRepository userRepository;

    public void createInvitation(GoalInvitationDto invitation) {
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new DataValidationException("Users cannot send invitations to themselves");
        }

        User inviter = userRepository.findById(invitation.getInviterId())
                .orElseThrow(() -> new EntityNotFoundException("Only existing users can send or accept invitations"));
        User invitedUser = userRepository.findById(invitation.getInvitedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Only existing users can send or accept invitations"));

        GoalInvitation goalInvitationEntity = goalInvitationMapper.toEntity(invitation);
        goalInvitationEntity.setInviter(inviter);
        goalInvitationEntity.setInvited(invitedUser);
        goalInvitationRepository.save(goalInvitationEntity);
    }

    public void acceptGoalInvitation(long invitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Goal invitation not found"));

        if (!goalInvitation.getStatus().equals(RequestStatus.PENDING)) {
            throw new DataValidationException("Invitation cannot be accepted");
        }

        Goal goal = goalInvitation.getGoal();
        User invitedUser = goalInvitation.getInvited();
        if (goal == null || invitedUser == null) {
            throw new EntityNotFoundException("Goal or user not found");
        }

        List<Goal> currentUserGoals = invitedUser.getGoals();
        if (currentUserGoals.size() >= GOALS_MAX_NUM) {
            throw new DataValidationException("User has reached maximum number of goals");
        }
        if (currentUserGoals.contains(goal)) {
            return;
        }

        currentUserGoals.add(goal);
        invitedUser.setGoals(currentUserGoals);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.setInvited(invitedUser);

        userRepository.save(invitedUser);
        goalInvitationRepository.save(goalInvitation);
    }
}

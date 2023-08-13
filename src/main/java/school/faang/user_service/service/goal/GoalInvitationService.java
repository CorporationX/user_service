package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalInvitationValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    private final GoalInvitationMapper goalInvitationMapper;

    private final GoalInvitationValidator goalInvitationValidator;

    private final UserService userService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateNewGoalInvitation(invitation);

        User inviter = userService.findUserById(invitation.getInviterId());
        User invitedUser = userService.findUserById(invitation.getInvitedUserId());

        GoalInvitation goalInvitationEntity = goalInvitationMapper.toEntity(invitation);
        goalInvitationEntity.setInviter(inviter);
        goalInvitationEntity.setInvited(invitedUser);
        goalInvitationRepository.save(goalInvitationEntity);
        return invitation;
    }

    public GoalInvitationDto acceptGoalInvitation(long invitationId) {
        GoalInvitation goalInvitation = getGoalInvitationById(invitationId);
        goalInvitationValidator.validateAcceptedGoalInvitation(goalInvitation);

        Goal goal = goalInvitation.getGoal();
        User invitedUser = goalInvitation.getInvited();

        List<Goal> currentUserGoals = invitedUser.getGoals();

        currentUserGoals.add(goal);
        invitedUser.setGoals(currentUserGoals);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.setInvited(invitedUser);

        userService.saveUser(invitedUser);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    private GoalInvitation getGoalInvitationById(long invitationId) {
        return goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Goal invitation not found"));
    }
}

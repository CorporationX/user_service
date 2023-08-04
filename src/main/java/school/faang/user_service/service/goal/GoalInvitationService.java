package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalInvitationValidation;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository repository;
    private final GoalInvitationValidation goalInvitationValidation;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper mapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidation.invitationValidationUser(invitation);

        GoalInvitation goalInvitation = mapper.toEntity(invitation);

        User inviter = userRepository.findById(invitation.getInviterId()).orElseThrow();
        User invited = userRepository.findById(invitation.getInvitedUserId()).orElseThrow();
        Goal goal = goalRepository.findById(invitation.getGoalId()).orElseThrow();

        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        return mapper.toDto(repository.save(goalInvitation));
    }
}

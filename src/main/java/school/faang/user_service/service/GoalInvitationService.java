package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.validator.GoalInvitationValidator;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    private final GoalInvitationValidator goalInvitationValidator;

    private final GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateGoalInvitation(invitation);
        goalInvitationRepository.save(goalInvitationMapper.toEntity(invitation));
    }

    public void acceptGoalInvitation(long invitationId) {
        goalInvitationValidator.validateAcceptedGoalInvitation(invitationId);
    }
}

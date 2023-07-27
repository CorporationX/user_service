package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private UserRepository userRepository;
    private GoalRepository goalRepository;
    private GoalInvitationRepository goalInvitationRepository;
    private GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        validateInvitation(invitationDto);
        GoalInvitation invitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
        return goalInvitationMapper.toDto(invitation);
    }

    private void validateInvitation(GoalInvitationDto invitation) {
        if (invitation.getId() == null || invitation.getId() < 1) {
            throw new DataValidException("Invitation illegal id");
        }
        if (!goalRepository.existsById(invitation.getGoalId())) {
            throw new DataValidException("Goal does not exist. Invitation Id: " + invitation.getId());
        }
        User inviter = userRepository.findById(invitation.getInviterId())
                .orElseThrow(() -> new DataValidException("Inviter does not exist, Id: " + invitation.getInviterId()));
        User invited = userRepository.findById(invitation.getInvitedUserId())
                .orElseThrow(() -> new DataValidException("Invited does not exist, Id: " + invitation.getInvitedUserId()));
        if (inviter.equals(invited)) {
            throw new DataValidException("Inviter and invited are equal. Invitation Id: " + invitation.getId());
        }
    }
}

package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        validateInvitation(invitationDto);
        GoalInvitation invitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
        return goalInvitationMapper.toDto(invitation);
    }

    private void validateInvitation(GoalInvitationDto invitation) {
        if (invitation.getId() == null || invitation.getId() < 1) {
            throw new DataValidException("Invitation illegal id");
        }
        if (goalInvitationRepository.existsById(invitation.getId())) {
            throw new DataValidException("Invitation already exist. Id: " + invitation.getId());
        }
        if (!goalRepository.existsById(invitation.getGoalId())) {
            throw new DataValidException("Goal does not exist. Invitation Id: " + invitation.getId());
        }
        if (!userRepository.existsById(invitation.getInviterId())) {
            throw new DataValidException("Inviter does not exist, Id: " + invitation.getInviterId());
        }
        if (!userRepository.existsById(invitation.getInvitedUserId())) {
            throw new DataValidException("Invited does not exist, Id: " + invitation.getInvitedUserId());
        }
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new DataValidException("Inviter and invited are equal. Invitation Id: " + invitation.getId());
        }
    }
}

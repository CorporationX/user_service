package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    public void createInvitation(GoalInvitationDto invitationDto) {
        if (invitationDto.inviterId() == null || invitationDto.invitedUserId() == null) {
            throw new DataValidationException("Inviter ID and Invited User ID cannot be null");
        } else if (Objects.equals(invitationDto.inviterId(), invitationDto.invitedUserId())) {
            throw new DataValidationException("Inviter ID and Invited User ID must be different");
        }
        if (!goalInvitationRepository.existsInvitedById(invitationDto.invitedUserId())) {
            throw new DataValidationException("Invited User doesn't exist in DB");
        } else if (!goalInvitationRepository.existsInviterById(invitationDto.inviterId())) {
            throw new DataValidationException("Inviter User doesn't exist in DB");
        }
        goalInvitationRepository.existsById();
    }
}
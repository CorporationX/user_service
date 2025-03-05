package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.utils.validationUtils.GoalInvitationValidation;

@RequiredArgsConstructor
@Service
@Slf4j
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidation goalInvitationValidation;

    public void createInvitation(GoalInvitationDto invitationDto) {
        goalInvitationValidation.validateGoalInvitationDtoForCreation(invitationDto);
        goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
        log.info("Goal invitation created successfully for: {}", invitationDto);
    }

    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.getReferenceById(id);
        User invitedUser = goalInvitation.getInvited();
        if (invitedUser.getReceivedGoalInvitations().size() >= MAX_ACTIVE_GOALS) {
            log.warn("User with ID {} has reached the active target limit", goalInvitation.getId());
            goalInvitation.setStatus(RequestStatus.REJECTED);
            return;
        }
        if (invitedUser.getReceivedGoalInvitations().contains(goalInvitation)) {
            log.warn("User with ID {} is already working on this goal", goalInvitation.getId());
            return;
        }
        goalInvitationValidation.validateGoalInvitationForAcceptance(goalInvitation);
        goalInvitation.getInvited().getReceivedGoalInvitations().add(goalInvitation);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        log.info("Goal invitation with ID: {} accepted successfully", id);
    }

    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.getReferenceById(id);
        goalInvitationValidation.validateGoalInvitationForRejection(goalInvitation);
        goalInvitation.getInvited().getReceivedGoalInvitations().remove(goalInvitation);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        log.info("Goal invitation with ID: {} rejected successfully", id);
    }
}
package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.utils.validationUtils.GoalInvitationValidation;

@RequiredArgsConstructor
@Service
@Slf4j
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidation goalInvitationValidation;

    public void createInvitation(GoalInvitationDto invitationDto) {
        goalInvitationValidation.validateGoalInvitationDtoInCreation(invitationDto);
        goalInvitationRepository.save(goalInvitationMapper.toEntity(invitationDto));
    }

    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.getReferenceById(id);
        if (goalInvitationValidation.validateGoalInvitationInAccept(goalInvitation)) {
            goalInvitation.getInvited().getReceivedGoalInvitations().add(goalInvitation);
            goalInvitation.setStatus(RequestStatus.ACCEPTED);
        }
    }
}
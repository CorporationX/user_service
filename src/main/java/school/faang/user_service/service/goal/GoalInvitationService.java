package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.validation.goal.GoalInvitationValidator;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final GoalInvitationRepository goalInvitationRepository;

    @Transactional
    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitationDto);
        goalInvitationValidator.validate(goalInvitation);
        return goalInvitationMapper.toDto(goalInvitationRepository.save(goalInvitation));
    }

    @Transactional
    public GoalInvitationDto acceptGoalInvitation(Long id) {
        GoalInvitation goalInvitation = findInvitation(id);
        goalInvitationValidator.validate(goalInvitation);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.getInvited().getGoals().add(goalInvitation.getGoal());
        return goalInvitationMapper.toDto(goalInvitation);
    }

    private GoalInvitation findInvitation(Long id) {
        return goalInvitationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Invalid request. Invitation with ID: " + id + " does not exist"));
    }
}

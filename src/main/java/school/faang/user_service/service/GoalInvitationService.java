package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.controller.goal.InvitationFilterDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper mapper;
    private final UserRepository userRepository;

    public GoalInvitationDto createInvitation(GoalInvitationDto dto) {
        if (Objects.isNull(dto.getInviterId()) || Objects.isNull(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("Inviter/invited user can't be null!");
        }

        if (dto.getInviterId().equals(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("Inviter and invited users can't be the same user!");
        }

        boolean inviterExists  = userRepository.existsById(dto.getInviterId());
        boolean invitedExists = userRepository.existsById(dto.getInvitedUserId());
        if (!(invitedExists && inviterExists)) {
            throw new IllegalArgumentException("Inviter and invited users are not registered in the system!");
        }

        GoalInvitation invitationToSave = mapper.toEntity(dto);
        GoalInvitation createdEntity = goalInvitationRepository.save(invitationToSave);
        return mapper.toDto(createdEntity);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow();
        User invited = userRepository.findById(goalInvitation.getInvited().getId()).orElseThrow();

        if (!invited.getGoals().contains(goalInvitation.getGoal())) {
            throw new IllegalStateException("Invited already working on this goal.");
        }

        if (invited.getSetGoals().size() > 3) {
            throw new IllegalStateException("Invited user's goals can't be greater than 3.");
        }

        invited.getSetGoals().add(goalInvitation.getGoal());
        userRepository.saveAndFlush(invited);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        GoalInvitation approvedInvitation = goalInvitationRepository.saveAndFlush(goalInvitation);
        return mapper.toDto(approvedInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow();
        goalInvitation.setStatus(RequestStatus.REJECTED);
        GoalInvitation rejectedInvitation = goalInvitationRepository.saveAndFlush(goalInvitation);
        return mapper.toDto(rejectedInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> allInvitations = goalInvitationRepository.findAll();
        List<GoalInvitation> filteredInvitations =  allInvitations.stream()
                .filter(invitation -> Objects.isNull(filter.getInvitedId()) || invitation.getInviter().getId().equals(filter.getInviterId()))
                .filter(invitation -> Objects.isNull(filter.getInviterId()) || invitation.getInvited().getId().equals(filter.getInvitedId()))
                .filter(invitation -> Objects.isNull(filter.getInvitedNamePattern()) || invitation.getInviter().getUsername().contains(filter.getInviterNamePattern()))
                .filter(invitation -> Objects.isNull(filter.getInvitedNamePattern()) || invitation.getInvited().getUsername().contains(filter.getInvitedNamePattern()))
                .filter(invitation -> Objects.isNull(filter.getStatus()) || invitation.getStatus().equals(filter.getStatus()))
                .toList();

        return mapper.toDtoList(filteredInvitations);
    }
}

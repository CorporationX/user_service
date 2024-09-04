package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper mapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto dto) {
        if (Objects.isNull(dto.getInviterId()) || Objects.isNull(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("Inviter/invited user can't be null!");
        }

        if (dto.getInviterId().equals(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("Inviter and invited users can't be the same user!");
        }

        List<User> inviterInvitedPair = userRepository.findAllById(List.of(dto.getInviterId(), dto.getInvitedUserId()));
        if (inviterInvitedPair.size() != 2) {
            throw new IllegalStateException("Both inviter and invited users need to be registered in the system!");
        }

        GoalInvitation invitationToSave = mapper.toEntity(dto);
        GoalInvitation savedInvitation = goalInvitationRepository.save(invitationToSave);
        return mapper.toDto(savedInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow();
        User invitedUser = goalInvitation.getInvited();
        Goal targetGoal = goalInvitation.getGoal();

        if(!goalRepository.existsById(targetGoal.getId())) {
            throw new IllegalStateException();
        }

        if (invitedUser.getSetGoals().contains(goalInvitation.getGoal())) {
            throw new IllegalStateException("Invited user already working on this goal.");
        }

        if (invitedUser.getSetGoals().size() > 3) {
            throw new IllegalStateException("Invited user's goals can't be greater than 3.");
        }

        invitedUser.getSetGoals().add(targetGoal);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        return mapper.toDto(goalInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow();
        Goal targetGoal = goalInvitation.getGoal();
        if (!goalRepository.existsById(targetGoal.getId())) {
            throw new IllegalStateException("Goal for this invitation doesn't exist.");
        }

        goalInvitation.setStatus(RequestStatus.REJECTED);
        return mapper.toDto(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        Long inviterIdFilter = filter.getInviterId();
        Long invitedIdFilter = filter.getInvitedId();
        String inviterNameFilter = filter.getInviterNamePattern();
        String invitedNameFilter = filter.getInvitedNamePattern();
        RequestStatus statusFilter = filter.getStatus();

        List<GoalInvitation> filteredInvitations = goalInvitationRepository.findAll();
        if (anyNull(filter.getInviterNamePattern(), filter.getInvitedNamePattern())) {
            filteredInvitations = filteredInvitations.stream()
                    .filter(invitation -> Objects.isNull(inviterIdFilter) || Objects.equals(invitation.getInviter().getId(), inviterIdFilter))
                    .filter(invitation -> Objects.isNull(invitedIdFilter) || Objects.equals(invitation.getInvited().getId(), invitedIdFilter))
                    .filter(invitation -> Objects.isNull(statusFilter) || Objects.equals(invitation.getStatus(), statusFilter))
                    .toList();
        } else if (anyNull(filter.getInviterId(), filter.getInvitedId())) {
            filteredInvitations = filteredInvitations.stream()
                    .filter(invitation -> Objects.isNull(inviterNameFilter) || Objects.equals(invitation.getInviter().getUsername(), inviterNameFilter))
                    .filter(invitation -> Objects.isNull(invitedNameFilter) || Objects.equals(invitation.getInvited().getUsername(), invitedNameFilter))
                    .filter(invitation -> Objects.equals(invitation.getStatus(), statusFilter))
                    .toList();
        }

        return mapper.toDtoList(filteredInvitations);
    }

    private boolean anyNull(Object... filterParams) {
        return Arrays.stream(filterParams).anyMatch(Objects::isNull);
    }
}

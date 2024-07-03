package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(@NotNull GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto.getInvitedUserId().equals(goalInvitationDto.getInviterId())) {
            throw new IllegalArgumentException("Exception invited user can`t be invitor ");
        }
        if (!userRepository.existsById(goalInvitationDto.getInviterId())) {
            throw new NoSuchElementException("User with id:" + goalInvitationDto.getInviterId() + " doesn't exist!");
        }
        if (!userRepository.existsById(goalInvitationDto.getInvitedUserId())) {
            throw new NoSuchElementException("User with id:" + goalInvitationDto.getInvitedUserId() + " doesn't exist!");
        }
        if (!goalRepository.existsById(goalInvitationDto.getGoalId())) {
            throw new NoSuchElementException("Goal with id:" + goalInvitationDto.getGoalId() + " doesn't exist!");
        }
        GoalInvitation savedInvitation = goalInvitationRepository.save(goalInvitationMapper.toEntity(goalInvitationDto));

        return goalInvitationMapper.toDto(savedInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation savedInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such goal invitation with id:" + id));

        User invited = savedInvitation.getInvited();
        if (invited.getReceivedGoalInvitations().size() > 3)
            throw new IllegalArgumentException("Exception invited user can`t have more than 3 goal invitations");
        invited.getGoals().add(savedInvitation.getGoal());
        savedInvitation.setStatus(RequestStatus.ACCEPTED);
        userRepository.save(invited);
        goalInvitationRepository.save(savedInvitation);
        return goalInvitationMapper.toDto(savedInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation savedInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such goal invitation with id:" + id));

        savedInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(savedInvitation);
        return goalInvitationMapper.toDto(savedInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();

        goalInvitations = filterByStatus(goalInvitations, filter.getStatus());
        goalInvitations = filterByInviterId(goalInvitations, filter.getInviterId());
        goalInvitations = filterByInvitedId(goalInvitations, filter.getInvitedId());
        goalInvitations = filterByInviterNamePattern(goalInvitations, filter.getInviterNamePattern());
        goalInvitations = filterByInvitedNamePattern(goalInvitations, filter.getInvitedNamePattern());

        return goalInvitations.stream().map(goalInvitationMapper::toDto).toList();
    }

    private List<GoalInvitation> filterByInviterNamePattern(List<GoalInvitation> goalInvitations, String inviterNamePattern) {
        if (inviterNamePattern == null || inviterNamePattern.isBlank()) {
            return goalInvitations;
        }
        return goalInvitations.stream()
                .filter(invintation -> invintation.getInviter().getUsername().matches(inviterNamePattern)).toList();
    }

    private List<GoalInvitation> filterByInvitedNamePattern(List<GoalInvitation> goalInvitations, String invitedNamePattern) {
        if (invitedNamePattern == null || invitedNamePattern.isBlank()) {
            return goalInvitations;
        }
        return goalInvitations.stream()
                .filter(invintation -> invintation.getInviter().getUsername().matches(invitedNamePattern)).toList();

    }

    private List<GoalInvitation> filterByInviterId(List<GoalInvitation> goalInvitations, Long inviterId) {
        if (inviterId == null || inviterId < 0) {
            return goalInvitations;
        }
        return goalInvitations.stream()
                .filter(invintation -> invintation.getInviter().getId() == inviterId).toList();
    }

    private List<GoalInvitation> filterByInvitedId(List<GoalInvitation> goalInvitations, Long invitedId) {
        if (invitedId == null || invitedId < 0) {
            return goalInvitations;
        }
        return goalInvitations.stream()
                .filter(invintation -> invintation.getInvited().getId() == invitedId).toList();
    }

    private List<GoalInvitation> filterByStatus(List<GoalInvitation> goalInvitations, RequestStatus requestStatus) {
        if (requestStatus == null) {
            return goalInvitations;
        }
        return goalInvitations.stream()
                .filter(invintation -> invintation.getStatus() == requestStatus).toList();
    }

}

package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
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
    private final UserRepository userRepository;

    public GoalInvitation createInvitation(GoalInvitation invitation) {
        return goalInvitationRepository.save(invitation);
    }

    public void acceptGoalInvitation(GoalInvitation invitation) {
        User invited = invitation.getInvited();
        invited.getGoals().add(invitation.getGoal());
        userRepository.save(invited);

        invitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.save(invitation);
    }

    public void rejectGoalInvitation(GoalInvitation invitation) {
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    public List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> filteredInvitations = goalInvitationRepository.findAll();
        if (Objects.isNull(filter)) {
            return filteredInvitations;
        }

        Long inviterIdFilter = filter.getInviterId();
        Long invitedIdFilter = filter.getInvitedId();
        String inviterNameFilter = filter.getInviterNamePattern();
        String invitedNameFilter = filter.getInvitedNamePattern();
        RequestStatus statusFilter = filter.getStatus();

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

        return filteredInvitations;
    }

    // Constant resource allocation for stream creation doesn't sound promising
    private boolean anyNull(Object... filterParams) {
        return Arrays.stream(filterParams).anyMatch(Objects::isNull);
    }
}

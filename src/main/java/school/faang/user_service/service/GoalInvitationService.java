package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;

    public List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> invitations = new ArrayList<>();
        goalInvitationRepository.findAll().forEach(invitations::add);

        if (invitations.isEmpty()) {
            throw new IllegalArgumentException("Invalid request. Goal invitation not found.");
        }

        return filterInvitation(filter, invitations);
    }

    private List<GoalInvitation> filterInvitation(InvitationFilterDto filter, List<GoalInvitation> invitations) {
        if (filter.getInviterNamePattern() != null) {
            invitations.removeIf(in -> !in.getInviter().getUsername().contains(filter.getInviterNamePattern()));
        }
        if (filter.getInvitedNamePattern() != null) {
            invitations.removeIf(in -> !in.getInvited().getUsername().contains(filter.getInvitedNamePattern()));
        }
        if (filter.getInviterId() != null) {
            invitations.removeIf(in -> in.getInviter().getId() != filter.getInviterId());
        }
        if (filter.getInvitedId() != null) {
            invitations.removeIf(in -> in.getInvited().getId() != filter.getInvitedId());
        }
        if (filter.getStatus() != null) {
            invitations.removeIf(in -> !in.getStatus().equals(filter.getStatus()));
        }
        return invitations;
    }
}

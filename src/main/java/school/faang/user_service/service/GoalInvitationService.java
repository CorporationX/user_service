package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final List<InvitationFilter> invitationFilters;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationRepository goalInvitationRepository;

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        if (invitations.isEmpty()) {
            return new ArrayList<>();
        }

        invitationFilters.stream()
                .filter(f -> f.isApplicable(filters))
                .forEach(f -> f.apply(invitations.stream(), filters));

        return invitations.stream().map(goalInvitationMapper::toDto).toList();
    }
}

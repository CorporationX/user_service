package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.RequestStatus;

public record InvitationFilterDto(
        String inviterNamePattern,
        String invitedNamePattern,
        Long inviterId,
        Long invitedId,
        RequestStatus status
) {
}

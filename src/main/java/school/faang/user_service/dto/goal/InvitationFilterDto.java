package school.faang.user_service.dto.goal;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record InvitationFilterDto(
        String inviterNamePattern,
        String invitedNamePattern,
        Long inviterId,
        Long invitedId,
        RequestStatus status
) {
}

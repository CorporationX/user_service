package school.faang.user_service.model.dto.goal;

import lombok.Builder;
import school.faang.user_service.model.entity.RequestStatus;

@Builder
public record InvitationFilterDto(
        String inviterNamePattern,
        String invitedNamePattern,
        Long inviterId,
        Long invitedId,
        RequestStatus status) {
}
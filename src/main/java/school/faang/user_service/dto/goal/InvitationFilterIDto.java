package school.faang.user_service.dto.goal;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Builder
@RequiredArgsConstructor
@Getter
public class InvitationFilterIDto {
    private final Long inviterId;
    private final Long invitedId;
    private final RequestStatus status;
}

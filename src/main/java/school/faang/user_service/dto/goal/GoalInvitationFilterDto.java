package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Getter
@RequiredArgsConstructor
public class GoalInvitationFilterDto {
    private final Long inviterId;
    private final Long invitedId;
    private final RequestStatus status;
}

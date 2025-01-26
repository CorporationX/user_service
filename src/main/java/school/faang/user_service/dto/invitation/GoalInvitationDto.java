package school.faang.user_service.dto.invitation;

import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Setter
public class GoalInvitationDto {
    private Long id;
    private Long inviterId;
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}

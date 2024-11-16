package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class GoalInvitationDto {
    private Long inviterId;
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}

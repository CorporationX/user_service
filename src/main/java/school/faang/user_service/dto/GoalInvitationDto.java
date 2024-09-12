package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@AllArgsConstructor
public class GoalInvitationDto {
    private Long id;
    private Long inviterId;
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}

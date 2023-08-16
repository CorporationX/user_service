package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
public class GoalInvitationDto {
    private Long id;
    private Long inviterId;
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}

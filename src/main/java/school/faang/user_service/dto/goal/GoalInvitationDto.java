package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {
    private Long id;
    private Long inviterUserId;
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}

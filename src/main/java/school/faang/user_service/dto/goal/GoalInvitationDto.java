package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
@Data
public class GoalInvitationDto {
    private Long id;
    @NotNull
    @Positive
    private Long inviterId;
    @NotNull
    @Positive
    private Long invitedUserId;
    @NotNull
    @Positive
    private Long goalId;
    private RequestStatus status;
}

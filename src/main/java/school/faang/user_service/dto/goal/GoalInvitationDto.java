package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GoalInvitationDto {
    private Long id;
    @NotNull(message = "Field cannot be blank")
    private Long inviterId;
    @NotNull(message = "Field cannot be blank")
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}
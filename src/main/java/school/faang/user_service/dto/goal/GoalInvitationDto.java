package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationDto {
    @Min(value = 0, message = "Id should be a positive value")
    private Long id;
    @NotNull(message = "Inviter cannot be null")
    private Long inviterId;
    @NotNull(message = "Invited cannot be null")
    private Long invitedUserId;
    @NotNull(message = "Goal cannot be null")
    private Long goalId;
    private RequestStatus status;
}

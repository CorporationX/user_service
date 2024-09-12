package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalInvitationDto {
    private Long id;

    @NotNull(message = "inviterId can't be null")
    @Positive(message = "inviterId must be a positive number")
    private Long inviterId;

    @NotNull(message = "invitedUserId can't be null")
    @Positive(message = "invitedUserId must be a positive number")
    private Long invitedUserId;

    @NotNull(message = "goalId can't be null")
    @Positive(message = "goalId must be a positive number")
    private Long goalId;

    @NotNull(message = "RequestStatus can't be null")
    private RequestStatus status;
}

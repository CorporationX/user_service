package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {
    private Long id;

    @NotNull(message = "inviterId shouldn't be null")
    private Long inviterId;

    @NotNull(message = "invitedUserId shouldn't be null")
    private Long invitedUserId;

    @NotNull(message = "goalId shouldn't be null")
    private Long goalId;

    private RequestStatus status;
}

package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@NoArgsConstructor
@Getter
@Setter
public class GoalInvitationDto {
    private Long id;

    @NotNull(message = "inviterId can't be null")
    private Long inviterId;

    @NotNull(message = "invitedId can't be null")
    private Long invitedUserId;

    @NotNull(message = "goalId can't be null")
    private Long goalId;

    private RequestStatus status;
}

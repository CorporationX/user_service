package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive(message = "inviterId can't be negative")
    private Long inviterId;

    @NotNull(message = "invitedId can't be null")
    @Positive(message = "invitedUserId can't be negative")
    private Long invitedUserId;

    @NotNull(message = "goalId can't be null")
    @Positive(message = "goalId can't be negative")
    private Long goalId;

    private RequestStatus status;
}

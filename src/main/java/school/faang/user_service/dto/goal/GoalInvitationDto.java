package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @NotNull
    private RequestStatus status;
}

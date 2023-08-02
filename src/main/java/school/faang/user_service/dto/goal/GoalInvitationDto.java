package school.faang.user_service.dto.goal;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationDto {

    @NotNull
    private Long id;
    @NotNull
    private Long inviterId;
    @NotNull
    private Long invitedUserId;

    private RequestStatus status;
}

package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {

    @Schema(description = "Идентификатор приглашения на цель")
    private Long id;

    @NotNull(message = "inviterId shouldn't be null")
    @Schema(description = "Идентификатор приглашающего пользователя")
    private Long inviterId;

    @NotNull(message = "invitedUserId shouldn't be null")
    @Schema(description = "Идентификатор приглашаемого пользователя")
    private Long invitedUserId;

    @NotNull(message = "goalId shouldn't be null")
    @Schema(description = "Идентификатор цели")
    private Long goalId;

    @Schema(description = "Статус приглашения на цель")
    private RequestStatus status;
}

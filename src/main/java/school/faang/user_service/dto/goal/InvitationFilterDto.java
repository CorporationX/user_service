package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class InvitationFilterDto {

    @Schema(description = "Имя приглашающего пользователя")
    private String inviterNamePattern;

    @Schema(description = "Имя приглашаемого пользователя")
    private String invitedNamePattern;

    @Schema(description = "Идентификатор приглашающего пользователя")
    private Long inviterId;

    @Schema(description = "Идентификатор приглашаемого пользователя")
    private Long invitedId;

    @Schema(description = "Статус приглашения на цель")
    private RequestStatus status;
}

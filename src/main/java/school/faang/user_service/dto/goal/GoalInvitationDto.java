package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Schema(description = "DTO Прглашения к участию в цели")
public class GoalInvitationDto {

    private static final String NOT_NULL_ID = "Поле Id не может быть равным нулю";
    private static final String POSITIVE_ID = "Поле Id должно быть положительным числом";

    @Schema(description = "Уникальный идентификатор приглашения", example = "1")
    private Long id;

    @Schema(description = "ID пользователя, отправившего приглашение", example = "100")
    @NotNull(message = NOT_NULL_ID)
    @Positive(message = POSITIVE_ID)
    private Long inviterId;

    @Schema(description = "ID приглашенного пользователя", example = "200")
    @NotNull(message = NOT_NULL_ID)
    @Positive(message = POSITIVE_ID)
    private Long invitedUserId;

    @Schema(description = "ID цели, к которой отправлено приглашение", example = "300")
    @NotNull(message = NOT_NULL_ID)
    @Positive(message = POSITIVE_ID)
    private Long goalId;

    @Schema(description = "Статус приглашения (например, PENDING, ACCEPTED, REJECTED)", example = "PENDING")
    private RequestStatus status;
}

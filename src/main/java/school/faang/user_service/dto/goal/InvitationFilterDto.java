package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.PositiveOrZero;
import school.faang.user_service.entity.RequestStatus;

public record InvitationFilterDto(
        @PositiveOrZero(message = "Inviter ID must be a positive number or zero")
        Long inviterId,

        @PositiveOrZero(message = "Invited ID must be a positive number or zero")
        Long invitedId,

        RequestStatus status
) {

}
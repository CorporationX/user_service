package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record InvitationFilterDto(

        String inviterNamePattern,

        String invitedNamePattern,

        @Min(value = 1, message = "Id can't be lower than 1")
        Long inviterId,

        @Min(value = 1, message = "Id can't be lower than 1")
        Long invitedId,

        RequestStatus status
) {
}

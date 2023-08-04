package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record InvitationFilterDto(

        @Size(max = 255, message = "Name can't be longer than 255")
        String inviterNamePattern,

        @Size(max = 255, message = "Name can't be longer than 255")
        String invitedNamePattern,

        Long inviterId,

        Long invitedId,

        RequestStatus status
) {
}

package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record GoalInvitationDto(
        Long id,

        @NotNull
        Long inviterId,

        @NotNull
        Long invitedUserId,

        @NotNull
        Long goalId,

        @NotNull
        RequestStatus status) {
}

package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import school.faang.user_service.model.RequestStatus;

@Builder
public record GoalInvitationDto(
        Long id,
        @Positive Long inviterId,
        @Positive Long invitedUserId,
        @Positive Long goalId,
        @NotNull RequestStatus status
) {
}

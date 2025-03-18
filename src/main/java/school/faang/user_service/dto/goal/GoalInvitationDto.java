package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import school.faang.user_service.entity.RequestStatus;

public record GoalInvitationDto(
        Long id,

        @NotNull(message = "Invited ID must not be null")
        @Positive(message = "Invited ID must be a positive number")
        Long inviterId,

        @NotNull(message = "Invited ID must not be null")
        @Positive(message = "Invited ID must be a positive number")
        Long invitedUserId,

        @NotNull(message = "Goal ID must not be null")
        @Positive(message = "Goal ID must be a positive number")
        Long goalId,

        RequestStatus status
) {}
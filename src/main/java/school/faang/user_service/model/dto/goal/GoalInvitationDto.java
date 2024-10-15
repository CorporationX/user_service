package school.faang.user_service.model.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

@Builder
public record GoalInvitationDto(
        @Null(message = "Goal invitation ID must be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long id,

        @NotNull(message = "Inviter ID must not be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long inviterId,

        @NotNull(message = "Invited user ID must not be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long invitedUserId,

        @NotNull(message = "Goal ID must not be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long goalId,

        @NotNull(message = "Request status ID must not be null", groups = {CreateGroup.class, UpdateGroup.class})
        RequestStatus status) {
}
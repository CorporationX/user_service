package school.faang.user_service.dto;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import school.faang.user_service.entity.RequestStatus;

public record GoalInvitationDto(@NonNull @Positive Long id, @NonNull @Positive Long inviterId, @NonNull @Positive Long invitedUserId,
                                @NonNull @Positive Long goalId, @NonNull RequestStatus status) { }

package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestFilterDto(
        @NotNull(message = "Not null")
        Long requesterId,
        @NotNull(message = "Not null")
        Long receiverId,
        RequestStatus status
) {
}

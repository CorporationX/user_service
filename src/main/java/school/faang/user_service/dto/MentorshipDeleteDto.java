package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;

public record MentorshipDeleteDto(
        @NotNull(message = "Mentor ID must not be null.")
        Long mentorId,
        @NotNull(message = "Mentee ID must not be null.")
        Long menteeId
) {
}

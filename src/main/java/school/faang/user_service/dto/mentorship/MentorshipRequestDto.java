package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;


public record MentorshipRequestDto(
    @NotNull
    Long mentorId,
    @NotNull
    Long menteeId
) {}

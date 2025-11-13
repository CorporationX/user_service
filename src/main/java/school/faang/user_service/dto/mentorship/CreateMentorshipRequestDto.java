package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateMentorshipRequestDto(
        @NotNull @Size(max = 2000) String description,
        @NotNull @Positive Long mentorId
) {
}
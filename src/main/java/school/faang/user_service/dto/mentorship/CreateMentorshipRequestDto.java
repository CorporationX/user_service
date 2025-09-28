package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;

public record CreateMentorshipRequestDto(
        @NotBlank(message = "Cannot be Empty")
        String description,
        Long mentorId
) {
}

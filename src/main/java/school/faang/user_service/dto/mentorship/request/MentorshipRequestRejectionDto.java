package school.faang.user_service.dto.mentorship.request;

import jakarta.validation.constraints.NotBlank;

public record MentorshipRequestRejectionDto(

        @NotBlank(message = "Rejection reason is required")
        String reason) {
}

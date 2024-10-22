package school.faang.user_service.dto.mentorship.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record MentorshipRequestCreationDto(

        @NotBlank(message = "Description with reason for request is required")
        String description,

        @NotNull(message = "Requester Id is required")
        @Positive(message = "Requester Id must be positive")
        Long requesterId,

        @NotNull(message = "Mentor Id is required")
        @Positive(message = "Mentor Id must be positive")
        Long receiverId) {
}

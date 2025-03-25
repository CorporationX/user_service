package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRequestDto(
        Long id,
        @NotBlank @Size(min = 50, max = 4096,
                message = "Requested description must be between 50 to 4096 characters long")
        String description,
        @NotNull String requesterUsername, @NotNull String receiverUsername,
        RequestStatus status, LocalDateTime createdAt, LocalDateTime updatedAt
) {
}
package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRejectionDto(
        Long id,
        @NotBlank @Size(min = 50, max = 4096,
                message = "Rejection reason must be between 50 to 4096 characters long")
        String rejectionReason,
        RequestStatus status, LocalDateTime createdAt, LocalDateTime updatedAt
) {
}
package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectionDto(
        @NotBlank(message = "Rejection reason cannot be empty")
        String reason
) {
}

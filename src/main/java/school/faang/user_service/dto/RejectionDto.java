package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RejectionDto(
        @NotNull
        @NotBlank
        String rejectionReason
) {
}

package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;

public record RejectionDto(
        @NotNull String reason
) {
}
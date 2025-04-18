package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Size;

public record RejectionDto(
        @Size(max = 255)
        String reason
) {
}

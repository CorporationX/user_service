package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import school.faang.user_service.dto.DtoValidationConstraints;

public record RejectionDto(@NotBlank(message = DtoValidationConstraints.MENTORSHIP_REJECTION_REASON_CONSTRAINT) String reason) {
}

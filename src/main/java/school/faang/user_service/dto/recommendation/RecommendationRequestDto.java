package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.*;
import lombok.Builder;
import school.faang.user_service.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationRequestDto(
        Long id,
        @NotBlank @Size(max = 4096) String message,
        @NotNull RequestStatus status,
        @NotNull List<Long> skillIds,
        @NotNull @Positive Long requesterId,
        @NotNull @Positive Long receiverId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

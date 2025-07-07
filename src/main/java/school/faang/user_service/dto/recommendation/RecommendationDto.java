package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.LocalDateTime;

public record RecommendationDto(
        Long id,
        @NonNull
        @NotNull
        Long authorId,
        @NonNull
        @NotNull
        Long receiverId,
        @NotBlank
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

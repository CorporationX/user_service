package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRecommendationDto(
        @NotNull
        Long receiverId,
        @NotBlank
        String content
) {
}

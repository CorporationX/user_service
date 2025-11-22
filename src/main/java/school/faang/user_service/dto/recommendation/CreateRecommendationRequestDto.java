package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRecommendationRequestDto(
        @NotBlank(message = "Recommendation request text cannot be empty")
        String message,
        @NotNull
        Long receiverId
) {
}

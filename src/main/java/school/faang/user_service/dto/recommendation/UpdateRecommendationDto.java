package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;

public record UpdateRecommendationDto(
        @NotBlank
        String content
) {
}

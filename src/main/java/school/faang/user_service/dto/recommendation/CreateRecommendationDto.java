package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

public record CreateRecommendationDto(
        @Nonnull
        @JsonProperty("receiverId") Long receiverId,
        @Nonnull
        @JsonProperty("content") String content
) {
}

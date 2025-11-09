package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationDto(
        @JsonProperty("id") Long id,
        @JsonProperty("authorId") Long authorId,
        @JsonProperty("receiverId") Long receiverId,
        @JsonProperty("content") String content
) {
}

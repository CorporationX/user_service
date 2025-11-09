package school.faang.user_service.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationEvent(
        @JsonProperty("recommendationId") Long recommendationId,
        @JsonProperty("authorId") Long authorId,
        @JsonProperty("receiverId") Long receiverId
) {
}

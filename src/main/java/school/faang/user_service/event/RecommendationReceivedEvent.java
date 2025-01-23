package school.faang.user_service.event;

import lombok.Builder;

@Builder
public record RecommendationReceivedEvent(Long recommendationId, Long receiverId, Long authorId) {
}
